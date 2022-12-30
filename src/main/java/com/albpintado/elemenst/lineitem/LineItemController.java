package com.albpintado.elemenst.lineitem;

import com.albpintado.elemenst.linelist.LineList;
import com.albpintado.elemenst.linelist.LineListRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/line-item")
@CrossOrigin
public class LineItemController {

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private LineListRepository lineListRepository;

    @GetMapping
    public ResponseEntity<List<LineItem>> getAll() {
        Sort sort = Sort.by(Direction.DESC, "id");
        return new ResponseEntity<>(this.lineItemRepository.findAll(sort), HttpStatus.OK);
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<List<LineItem>> getAllByList(@PathVariable String id) {
        Sort sort = Sort.by(Direction.DESC, "id");
        return new ResponseEntity<>(this.lineItemRepository.findAllByLineListId(Long.valueOf(id), sort), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LineItem> create(@RequestBody LineItemDto lineItemDto) {
        return new ResponseEntity<>(lineItemRepository.save(setLineItem(lineItemDto)), HttpStatus.OK);
    }

    private LineItem setLineItem(LineItemDto lineItemDto) {
        LineItem lineItem = new LineItem();
        if (lineItemDto.getContent() != null) {
            lineItem.setContent(lineItemDto.getContent());
        }
        if (lineItemDto.getCreationDate() != null) {
            lineItem.setCreationDate(getLocalDateFromString(lineItemDto.getCreationDate()));
        }
        if (lineItemDto.getLineListId() != null) {
            lineItem.setLineList(getLineList(lineItemDto.getLineListId()));
        }
        return lineItem;
    }

    private LineList getLineList(Long lineListId) {
        Optional<LineList> optionalLineList = lineListRepository.findById(lineListId);
        return optionalLineList.orElse(null);
    }

    private LocalDate getLocalDateFromString(String dateAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(
                Locale.US);
        return LocalDate.parse(dateAsString, formatter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineItem> update(@PathVariable String id, @RequestBody LineItemDto lineItemDto) {
        Optional<LineItem> optionalLineItem = lineItemRepository.findById(Long.valueOf(id));
        if (optionalLineItem.isPresent()) {
            LineItem updatedLineItem = optionalLineItem.get();
            if (lineItemDto.getContent() != null) {
                updatedLineItem.setContent(lineItemDto.getContent());
            }
            return new ResponseEntity<>(lineItemRepository.save(updatedLineItem), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<LineItem> complete(@PathVariable String id) {
        Optional<LineItem> optionalLineItem = lineItemRepository.findById(Long.valueOf(id));
        if (optionalLineItem.isPresent()) {
            LineItem updatedLineItem = optionalLineItem.get();
            updatedLineItem.setCompleted(!updatedLineItem.getIsCompleted());
            return new ResponseEntity<>(lineItemRepository.save(updatedLineItem), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/pin")
    public ResponseEntity<LineItem> pin(@PathVariable String id) {
        Optional<LineItem> optionalLineItem = lineItemRepository.findById(Long.valueOf(id));
        if (optionalLineItem.isPresent()) {
            LineItem updatedLineItem = optionalLineItem.get();
            updatedLineItem.setPinned(!updatedLineItem.getIsPinned());
            return new ResponseEntity<>(lineItemRepository.save(updatedLineItem), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        lineItemRepository.findById(Long.valueOf(id)).ifPresent(lineItem -> lineItemRepository.delete(lineItem));
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
