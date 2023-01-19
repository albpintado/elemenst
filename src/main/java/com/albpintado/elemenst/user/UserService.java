package com.albpintado.elemenst.user;

import com.albpintado.elemenst.exception.ExceptionMessages;
import com.albpintado.elemenst.exception.ExistingUserNameException;
import com.albpintado.elemenst.exception.InvalidPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<List<User>> getAll() {
        return new ResponseEntity<>(this.userRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<User> getCurrent() {
        User user = getUserByUserName();

        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<User> create(UserDto userDto) throws ExistingUserNameException, InvalidPassword {
        User user = createUserEntity(userDto);
        return new ResponseEntity<>(this.userRepository.save(user), HttpStatus.CREATED);
    }

    private User createUserEntity(UserDto userDto) throws ExistingUserNameException, InvalidPassword {
        if (userNameExists(userDto.getUserName())) {
            throw new ExistingUserNameException
                    ("Username " + userDto.getUserName() + " already exists.");
        }
        User user = new User();
        user.setUserName(userDto.getUserName());
        if (isPasswordValid(userDto.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            return user;
        }
        throw new InvalidPassword(ExceptionMessages.INVALID_PASSWORD.getMessage());
    }

    private boolean userNameExists(String userName) {
        Optional<User> optionalUser = this.userRepository.findOneByUserName(userName);
        return optionalUser.isPresent();
    }

    public ResponseEntity<User> update(UserDto userDto) {
        User user = getUserByUserName();
        if (user != null) {
            if (userDto.getUserName() != null) {
                user.setUserName(userDto.getUserName());
            }
            if (userDto.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
            this.userRepository.save(user);

            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<Object> delete() {
        User user = getUserByUserName();
        if (user != null) {
            this.userRepository.delete(user);
            SecurityContextHolder.clearContext();
            SecurityContextHolder.getContext().setAuthentication(null);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private User getUserByUserName() {
        String contextUserName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Optional<User> optionalLineList = userRepository.findOneByUserName(contextUserName);
        return optionalLineList.orElse(null);
    }

    private boolean isPasswordValid(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";
        return password.length() > 7 && password.length() < 19 && password.matches(pattern);
    }
}
