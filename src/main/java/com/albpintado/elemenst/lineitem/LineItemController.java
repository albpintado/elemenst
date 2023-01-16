package com.albpintado.elemenst.lineitem;

import com.albpintado.elemenst.exception.ForbiddenAccessException;
import com.albpintado.elemenst.exception.NotExistException;
import com.albpintado.elemenst.exception.UserNotFoundException;
import com.albpintado.elemenst.linelist.LineList;
import com.albpintado.elemenst.linelist.LineListRepository;
import com.albpintado.elemenst.user.User;
import com.albpintado.elemenst.user.UserRepository;
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
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/api/v1/line-item")
@CrossOrigin
public class LineItemController {

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private LineListRepository lineListRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<LineItem>> getAllByUser() {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Optional<User> optionalUser = this.userRepository.findOneByUserName(currentUserName);
        if (optionalUser.isPresent()) {
            Long userId = optionalUser.get().getId();
            Sort sort = Sort.by(Direction.DESC, "id");
            return new ResponseEntity<>(this.lineItemRepository.findAllByLineListUserId(userId, sort), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<List<LineItem>> getAllByList(@PathVariable String listId) {
        String userNameFromUserLogged = SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                .toString();
        Optional<User> optionalUser = this.userRepository.findOneByUserName(userNameFromUserLogged);
        if (optionalUser.isPresent()) {
            Optional<LineList> lineList = this.lineListRepository.findById(Long.valueOf(listId));
            if (lineList.isPresent()) {
                Long lineListUserId = lineList.get().getUser().getId();
                Long id = optionalUser.get().getId();
                if (lineListUserId.equals(id)) {
                    Sort sort = Sort.by(Direction.DESC, "id");
                    return new ResponseEntity<>(this.lineItemRepository.findAllByLineListId(Long.valueOf(listId), sort),
                            HttpStatus.OK);
                }
            }

        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<LineItem> create(@RequestBody LineItemDto lineItemDto)
            throws ForbiddenAccessException, NotExistException {
        return new ResponseEntity<>(lineItemRepository.save(setLineItem(lineItemDto)), HttpStatus.OK);
    }

    @ExceptionHandler({ConstraintViolationException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private LineItem setLineItem(LineItemDto lineItemDto) throws ForbiddenAccessException, NotExistException {
        String newContent = lineItemDto.getContent();
        String newCreationDate = lineItemDto.getCreationDate();
        Long newLineListId = lineItemDto.getLineListId();

        LineItem lineItem = new LineItem();

        if (newContent != null) {
            lineItem.setContent(newContent);
        }
        if (newCreationDate != null) {
            lineItem.setCreationDate(getLocalDateFromString(newCreationDate));
        }
        if (newLineListId != null) {
            lineItem.setLineList(getLineList(newLineListId));
        }
        return lineItem;
    }

    private boolean matchesLineListUserIdWithCurrent(LineList lineList) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Optional<User> optionalCurrentUser = userRepository.findOneByUserName(currentUserName);
        return optionalCurrentUser.isPresent() && optionalCurrentUser.get().getId().equals(lineList.getUser().getId());
    }

    private LineList getLineList(Long lineListId) throws ForbiddenAccessException, NotExistException {
        Optional<LineList> optionalLineList = lineListRepository.findById(lineListId);
        if (!optionalLineList.isPresent()) {
            throw new NotExistException("List with id " + lineListId + " does not exists.");
        }
        if (!matchesLineListUserIdWithCurrent(optionalLineList.get())) {
            throw new ForbiddenAccessException("Current user can not access other users lists.");
        }
        return optionalLineList.get();
    }

    private LocalDate getLocalDateFromString(String dateAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(
                Locale.US);
        return LocalDate.parse(dateAsString, formatter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineItem> update(@PathVariable String id, @RequestBody LineItemDto lineItemDto)
            throws UserNotFoundException {
        Optional<LineItem> optionalLineItem = lineItemRepository.findById(Long.valueOf(id));
        User currentUser = getCurrentUser();
        if (optionalLineItem.isPresent() && optionalLineItem.get().getLineList().getUser().getId()
                .equals(currentUser.getId())) {
            LineItem updatedLineItem = optionalLineItem.get();
            if (lineItemDto.getContent() != null) {
                updatedLineItem.setContent(lineItemDto.getContent());
            }
            return new ResponseEntity<>(lineItemRepository.save(updatedLineItem), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<LineItem> complete(@PathVariable String id) throws UserNotFoundException {
        Optional<LineItem> optionalLineItem = lineItemRepository.findById(Long.valueOf(id));
        User currentUser = getCurrentUser();
        if (optionalLineItem.isPresent() && optionalLineItem.get().getLineList().getUser().getId()
                .equals(currentUser.getId())) {
            LineItem updatedLineItem = optionalLineItem.get();
            updatedLineItem.setCompleted(!updatedLineItem.getIsCompleted());
            return new ResponseEntity<>(lineItemRepository.save(updatedLineItem), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    private User getCurrentUser() throws UserNotFoundException {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Optional<User> optionalLineList = userRepository.findOneByUserName(currentUserName);
        if (optionalLineList.isPresent()) {
            return optionalLineList.get();
        }
        throw new UserNotFoundException("User '" + currentUserName + "' does not exists.");
    }

    @PutMapping("/{id}/pin")
    public ResponseEntity<LineItem> pin(@PathVariable String id) throws UserNotFoundException {
        Optional<LineItem> optionalLineItem = lineItemRepository.findById(Long.valueOf(id));
        User currentUser = getCurrentUser();
        if (optionalLineItem.isPresent() && optionalLineItem.get().getLineList().getUser().getId()
                .equals(currentUser.getId())) {
            LineItem updatedLineItem = optionalLineItem.get();
            updatedLineItem.setPinned(!updatedLineItem.getIsPinned());
            return new ResponseEntity<>(lineItemRepository.save(updatedLineItem), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) throws UserNotFoundException {
        Optional<LineItem> optionalLineItem = lineItemRepository.findById(Long.valueOf(id));
        User currentUser = getCurrentUser();
        if (optionalLineItem.isPresent() && optionalLineItem.get().getLineList().getUser().getId()
                .equals(currentUser.getId())) {
            lineItemRepository.delete(optionalLineItem.get());
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }
}
