package com.example.demo.ServiceTesting;

import com.example.demo.buisnessUsage.users.structure.User;
import com.example.demo.buisnessUsage.users.structure.UserRepository;
import com.example.demo.buisnessUsage.users.structure.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@ExtendWith(MockitoExtension.class)
public class UserServiceTesting {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final String validTestUid = "2F2RMuYb+/IyaNJ-fOKOhWqO_sPv";


    // --- UPSERT ---

    @Test
    public void test_CreateUser_Success() {
        // Arrange
        User expectedUser = new User("id", "key", validTestUid, Instant.now());

        when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);

        // Act
        User result = userService.createUser(validTestUid);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.argThat(user ->
                user.getUid().equals(validTestUid)
        ));
    }

    @Test
    public void test_CreateUser_Fail_1() {
        // Arrange
        String uid = "invalid_uid";

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.createUser(uid));
    }

    @Test
    public void test_CreateUser_Fail_2() {
        // Arrange
        String uid = "   ";

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.createUser(uid));
    }


    // --- DOES_USER_EXIST ---

    @Test
    public void test_DoesUserExist_Success() {
        //Arrange
        User defaultUser = new User(
                "default_id",
                "default_generatedKey",
                validTestUid,
                Instant.now()
        );
        when(userRepository.findByUid(Mockito.any(String.class))).thenReturn(defaultUser);

        //Apply
        Boolean doesUserExist = userService.doesUserExist(validTestUid);

        //Assert
        Assertions.assertThat(doesUserExist).isNotNull();
        Mockito.verify(userRepository, Mockito.times(1)).findByUid(validTestUid);
    }

    @Test
    public void test_DoesUserExist_Fail_1() {
        // Arrange
        String uid = "invalid_uid";

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.doesUserExist(uid));
    }

    @Test
    public void test_DoesUserExist_Fail_2() {
        // Arrange
        String uid = "   ";

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.doesUserExist(uid));
    }


    // --- GET_USER ---

    @Test
    public void test_GetUser_Success() {
        //Arrange
        User defaultUser = new User(
                "default_id",
                "default_generatedKey",
                validTestUid,
                Instant.now()
        );
        when(userRepository.findByUid(Mockito.any(String.class))).thenReturn(defaultUser);

        //Apply
        Optional<User> optionalUser = userService.getUser(validTestUid);
        User getUser = optionalUser.orElse(null);

        //Assert
        Assertions.assertThat(getUser).isNotNull();
        Mockito.verify(userRepository, Mockito.times(1)).findByUid(validTestUid);
    }

    @Test
    public void test_GetUser_Fail_1() {
        // Arrange
        String uid = "invalid_uid";

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.getUser(uid));
    }

    @Test
    public void test_GetUser_Fail_2() {
        // Arrange
        String uid = "   ";

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.getUser(uid));
    }


    // --- REMOVE_USER ---

    @Test
    public void test_RemoveUser_Success() {
        // Arrange
        // Act
        userService.removeUser(validTestUid);

        // Assert - Adjusted to match deleteById since your service uses deleteById(uid)
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(validTestUid);
    }

    @Test
    public void test_RemoveUser_Fail_1() {
        // Arrange
        String targetUid = "invalid_uid";

        // Act & Assert - Expecting IllegalArgumentException based on your stack trace
        assertThrows(IllegalArgumentException.class, () -> userService.removeUser(targetUid));
    }

    @Test
    public void test_RemoveUser_Fail_2() {
        // Arrange
        String targetUid = "   ";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.removeUser(targetUid));
    }
}