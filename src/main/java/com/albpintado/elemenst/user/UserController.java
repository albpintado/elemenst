package com.albpintado.elemenst.user;

import com.albpintado.elemenst.exception.ExistingUserNameException;
import com.albpintado.elemenst.exception.InvalidPassword;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<User>> getAll() {
        return this.userService.getAll();
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrent() {
        return this.userService.getCurrent();
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserDto userDto)
            throws ExistingUserNameException, InvalidPassword {
        return this.userService.create(userDto);
    }

    @PutMapping()
    public ResponseEntity<User> update(@RequestBody UserDto userDto) {
        return this.userService.update(userDto);
    }

    @DeleteMapping()
    public ResponseEntity<Object> delete() {
        return this.userService.delete();
    }
}
