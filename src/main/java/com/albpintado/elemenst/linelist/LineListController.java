package com.albpintado.elemenst.linelist;

import com.albpintado.elemenst.lineitem.LineItemRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/line-list")
@CrossOrigin
public class LineListController {

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private LineListRepository lineListRepository;

    @GetMapping
    public ResponseEntity<List<LineList>> getAll() {
        Sort sort = Sort.by(Direction.DESC, "id");
        return new ResponseEntity<>(this.lineListRepository.findAll(sort), HttpStatus.OK);
    }

    @ExceptionHandler({ConstraintViolationException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PostMapping
    public ResponseEntity<LineList> create(@RequestBody LineListDto lineListDto) {
        return new ResponseEntity<>(lineListRepository.save(setLineList(lineListDto)), HttpStatus.OK);
    }

    private LineList setLineList(LineListDto lineListDto) {
        LineList lineList = new LineList();
        if (lineListDto.getName() != null) {
            lineList.setName(lineListDto.getName());
        }
        if (lineListDto.getCreationDate() != null) {
            lineList.setCreationDate(getLocalDateFromString(lineListDto.getCreationDate()));
        }
        return lineList;
    }

    private LocalDate getLocalDateFromString(String dateAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(
                Locale.US);
        return LocalDate.parse(dateAsString, formatter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineList> update(@PathVariable String id, @RequestBody LineListDto lineListDto) {
        Optional<LineList> optionalLineItem = lineListRepository.findById(Long.valueOf(id));
        if (optionalLineItem.isPresent()) {
            LineList updatedLineList = optionalLineItem.get();
            if (lineListDto.getName() != null) {
                updatedLineList.setName(lineListDto.getName());
            }
            return new ResponseEntity<>(lineListRepository.save(updatedLineList), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        lineListRepository.findById(Long.valueOf(id)).ifPresent(lineList -> lineListRepository.delete(lineList));
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
