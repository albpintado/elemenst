package com.albpintado.elemenst.user;

import com.albpintado.elemenst.exception.ExistingUserNameException;
import com.albpintado.elemenst.exception.InvalidPassword;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import java.util.ArrayList;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin("http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAll() {
        return this.userService.getAll();
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrent() {
        return this.userService.getCurrent();
    }

    @ExceptionHandler({ConstraintViolationException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidPassword.class)
    public ResponseEntity<Object> handleConstraintViolation(InvalidPassword ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PostMapping
    public ResponseEntity<User> createOne(@RequestBody UserDto userDto)
            throws ExistingUserNameException, InvalidPassword {
      if (isPasswordValid(userDto.getPassword())) {
          return this.userService.create(userDto);
      }
      throw new InvalidPassword("Password must have between 8 and 18 characters and include at least one uppercase letter, one lowercase letter, one number and one special character");
//      return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PutMapping()
    public ResponseEntity<User> update(@RequestBody UserDto userDto) {
        return this.userService.update(userDto);
    }

    @DeleteMapping()
    public ResponseEntity<Object> delete() {
        return this.userService.delete();
    }

    public boolean isPasswordValid(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";
        return password.length() > 7 && password.length() < 19 && password.matches(pattern);
    }
}
