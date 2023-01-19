package com.albpintado.elemenst.linelist;

import com.albpintado.elemenst.exception.UserNotFoundException;
import com.albpintado.elemenst.lineitem.LineItemRepository;
import com.albpintado.elemenst.user.User;
import com.albpintado.elemenst.user.UserRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/api/v1/line-list")
@CrossOrigin
public class LineListController {

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private LineListRepository lineListRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<LineList>> getAll() {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Sort sort = Sort.by(Direction.DESC, "id");
        if (currentUserName.equals("admin")) {
            return new ResponseEntity<>(this.lineListRepository.findAll(sort), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/user")
    public ResponseEntity<List<LineList>> getAllByUser() {
        String userNameFromUserLogged = SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                .toString();
        Optional<User> optionalUser = this.userRepository.findOneByUserName(userNameFromUserLogged);
        if (optionalUser.isPresent()) {
            Long id = optionalUser.get().getId();
            Sort sort = Sort.by(Direction.DESC, "id");
            List<LineList> userLineLists = this.lineListRepository.findAllByUserId(id, sort);
            return new ResponseEntity<>(userLineLists, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<LineList> create(@RequestBody LineListDto lineListDto) throws UserNotFoundException {
        return new ResponseEntity<>(lineListRepository.save(setLineList(lineListDto)), HttpStatus.OK);
    }

    private LineList setLineList(LineListDto lineListDto) throws UserNotFoundException {
        LineList lineList = new LineList();
        User user = getCurrentUser();
        if (lineListDto.getName() != null) {
            lineList.setName(lineListDto.getName());
        }
        if (lineListDto.getCreationDate() != null) {
            lineList.setCreationDate(getLocalDateFromString(lineListDto.getCreationDate()));
        }
        lineList.setUser(user);

        return lineList;
    }

    private User getCurrentUser() throws UserNotFoundException {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Optional<User> optionalLineList = userRepository.findOneByUserName(currentUserName);
        if (optionalLineList.isPresent()) {
            return optionalLineList.get();
        }
        throw new UserNotFoundException("User '" + currentUserName + "' does not exists.");
    }

    private LocalDate getLocalDateFromString(String dateAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(
                Locale.US);
        return LocalDate.parse(dateAsString, formatter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineList> update(@PathVariable String id, @RequestBody LineListDto lineListDto)
            throws UserNotFoundException {
        Optional<LineList> optionalLineList = lineListRepository.findById(Long.valueOf(id));
        User currentUser = getCurrentUser();
        if (optionalLineList.isPresent() && optionalLineList.get().getUser().getId().equals(currentUser.getId())) {
            LineList updatedLineList = optionalLineList.get();
            if (lineListDto.getName() != null) {
                updatedLineList.setName(lineListDto.getName());
            }
            return new ResponseEntity<>(lineListRepository.save(updatedLineList), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) throws UserNotFoundException {
        Optional<LineList> optionalLineList = lineListRepository.findById(Long.valueOf(id));
        User currentUser = getCurrentUser();
        if (optionalLineList.isPresent() && optionalLineList.get().getUser().getId().equals(currentUser.getId())) {
            lineListRepository.delete(optionalLineList.get());
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }
}
