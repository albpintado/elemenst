package com.albpintado.elemenst.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.albpintado.elemenst.exception.ExistingUserNameException;
import com.albpintado.elemenst.exception.InvalidPassword;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@WithMockUser(username = "alberto")
@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void givenGetCurrentUser_WhenUserExists_ReturnsOkResponse() {
        UserDto userDto = createUserDto("alberto", "Passw0rd!");
        User expectedUser = createUserEntity(userDto);
        findOneUserMock("alberto");

        ResponseEntity<User> userResponseEntity = this.userService.getCurrent();

        assertThat(userResponseEntity.getBody()).usingRecursiveComparison().ignoringFields("password")
                .isEqualTo(expectedUser);
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenGetCurrentUser_WhenUserNotExists_ReturnsNotFoundResponse() {
        findOneNullMock();

        ResponseEntity<User> userResponseEntity = this.userService.getCurrent();

        assertThat(userResponseEntity.getBody()).isNull();
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void givenCreate_WhenUserNameNotExists_ReturnsCreatedResponse() throws ExistingUserNameException, InvalidPassword {
        UserDto userDto = createUserDto("luis", "Passw0rd!");
        User expectedUser = createUserEntity(userDto);
        saveUserMock(expectedUser);

        ResponseEntity<User> userResponseEntity = this.userService.create(userDto);

        assertThat(userResponseEntity.getBody()).usingRecursiveComparison().ignoringFields("password")
                .isEqualTo(expectedUser);
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void givenCreate_WhenPasswordIsNotValid_ThrowsException() {
        UserDto userDto = createUserDto("luis", "Passw0rd!");
        findOneUserMock(userDto.getUserName());

        assertThrows(ExistingUserNameException.class, () -> this.userService.create(userDto));
    }

    @Test
    void givenCreate_WhenUserNameExists_ThrowsException() {
        UserDto userDto = createUserDto("luis", "password");
        findOneNullMock();

        assertThrows(InvalidPassword.class, () -> this.userService.create(userDto));
    }

    @Test
    void givenUpdate_WhenUserExists_ReturnsUpdatedUser() {
        UserDto userDto = createUserDto("luis", "Passw0rd!");
        UserDto updateUserDto = createUserDto("alberto", "Albert0!");
        User dbUser = createUserEntity(userDto);
        findOneUserMock(userDto.getUserName());

        User expectedUser = updateUserMock(dbUser, updateUserDto);

        ResponseEntity<User> userResponseEntity = this.userService.update(updateUserDto);

        assertThat(userResponseEntity.getBody()).usingRecursiveComparison().ignoringFields("password")
                .isEqualTo(expectedUser);
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenUpdate_WhenUserNotExists_ReturnsNullResponse() {
        UserDto updateUserDto = createUserDto("alberto", "Albert0!");
        findOneNullMock();

        ResponseEntity<User> userResponseEntity = this.userService.update(updateUserDto);

        assertThat(userResponseEntity.getBody()).isNull();
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void givenDelete_WhenUserExists_ReturnsOkResponse() {
        UserDto userDto = createUserDto("luis", "Passw0rd!");
        findOneUserMock(userDto.getUserName());

        ResponseEntity<?> userResponseEntity = this.userService.delete();

        assertThat(userResponseEntity.getBody()).isNull();
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenDelete_WhenUserNotExists_ReturnsNoContentResponse() {
        findOneNullMock();

        ResponseEntity<?> userResponseEntity = this.userService.delete();

        assertThat(userResponseEntity.getBody()).isNull();
        assertThat(userResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private void findOneUserMock(String userName) {
        Optional<User> optionalUser = getOptionalUser(userName);
        Mockito.when(this.userRepository.findOneByUserName(anyString())).thenReturn(optionalUser);
    }

    private void findOneNullMock() {
        Mockito.when(this.userRepository.findOneByUserName(anyString())).thenReturn(Optional.empty());
    }

    private void saveUserMock(User user) {
        Mockito.when(this.userRepository.save(any(User.class))).thenReturn(user);
    }

    private User updateUserMock(User dbUser, UserDto updateUserDto) {
        User expectedUser = new User();
        expectedUser.setId(dbUser.getId());
        expectedUser.setUserName(updateUserDto.getUserName());
        expectedUser.setPassword(updateUserDto.getPassword());
        expectedUser.setLineLists(dbUser.getLineLists());

        saveUserMock(expectedUser);

        return expectedUser;
    }

    private Optional<User> getOptionalUser(String userName) {
        User user = new User();
        user.setId(1L);
        user.setUserName(userName);
        user.setPassword("Passw0rd!");
        user.setLineLists(new ArrayList<>());
        return Optional.of(user);
    }

    private UserDto createUserDto(String userName, String password) {
        UserDto userDto = new UserDto();
        userDto.setUserName(userName);
        userDto.setPassword(password);

        return userDto;
    }

    private User createUserEntity(UserDto userDto) {
        User user = new User();
        user.setId(1L);
        user.setUserName(userDto.getUserName());
        user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
        user.setLineLists(new ArrayList<>());

        return user;
    }
}
