package com.example.demo.ServiceTesting;

import com.example.demo.User.structure.User;
import com.example.demo.User.structure.UserRepository;
import com.example.demo.User.structure.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.when;

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@ExtendWith(MockitoExtension.class)
public class UserServiceTesting {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void test_CreateUser() {
        // Arrange
        String uid = "default_id";
        User expectedUser = new User("id", "key", uid, Instant.now());

        when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);

        // Act
        User result = userService.createUser(uid);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.argThat(user ->
                user.getUid().equals(uid)
        ));
    }

    @Test
    public void test_DoesUserExist() {
        //Arrange
        User defaultUser = new User(
                "default_id",
                "default_generatedKey",
                "default_uid",
                Instant.now()
        );
        when(userRepository.findByUid(Mockito.any(String.class))).thenReturn(defaultUser);

        //Apply
        Boolean doesUserExist = userService.doesUserExist("default_uid");

        //Assert
        Assertions.assertThat(doesUserExist).isNotNull();
        Mockito.verify(userRepository, Mockito.times(1)).findByUid("default_uid");
    }

    @Test
    public void test_GetUser() {
        //Arrange
        User defaultUser = new User(
                "default_id",
                "default_generatedKey",
                "default_uid",
                Instant.now()
        );
        when(userRepository.findByUid(Mockito.any(String.class))).thenReturn(defaultUser);

        //Apply
        Optional<User> optionalUser = userService.getUser("default_uid");
        User getUser = optionalUser.orElse(null);

        //Assert
        Assertions.assertThat(getUser).isNotNull();
        Mockito.verify(userRepository, Mockito.times(1)).findByUid("default_uid");
    }

    @Test
    public void test_RemoveUser() {
        // Arrange
        String targetUid = "default_uid";
        User existingUser = new User(
                "default_id",
                "default_generatedKey",
                targetUid,
                Instant.now()
        );
        when(userRepository.findByUid(targetUid)).thenReturn(existingUser);

        // Act
        userService.removeUser(targetUid);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).delete(
                Mockito.argThat(user -> user.getUid().equals(targetUid))
        );
    }
}
