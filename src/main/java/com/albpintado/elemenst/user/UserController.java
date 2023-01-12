package com.albpintado.elemenst.user;

import com.albpintado.elemenst.exception.ExistingUserNameException;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
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
    public List<User> getAll() {
        return this.userService.getAll();
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrent() {
        return this.userService.getCurrent();
    }

    @PostMapping
    public ResponseEntity<User> createOne(@RequestBody UserDto userDto) throws ExistingUserNameException {
      if (isPasswordValid(userDto.getPassword())) {
        return this.userService.create(userDto);
      }
      return null;
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
