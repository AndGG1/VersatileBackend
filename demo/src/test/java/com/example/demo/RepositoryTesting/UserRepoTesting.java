package com.example.demo.RepositoryTesting;

import com.example.demo.User.structure.User;
import com.example.demo.User.structure.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@DataMongoTest
public class UserRepoTesting {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void test_FindById() {
        //Initial Config.
        userRepository.deleteAll();

        //Arrange
        String id_1 = "test_uid";
        User newUser = new User(
                "test_id",
                "test_generatedKey",
                "test_uid",
                Instant.now()
        );

        //Act
        userRepository.save(newUser);

        //Assert
        var result_1 = userRepository.findByUid(id_1);
        Assertions.assertThat(result_1).isNotNull();
    }

    @Test
    public void test_Save() {
        //Initial Config.
        userRepository.deleteAll();

        //Arrange
        User newUser = new User(
                "test_id",
                "test_generatedKey",
                "test_uid",
                Instant.now()
        );

        //Act
        User res = userRepository.save(newUser);

        //Assert
        Assertions.assertThat(res).isNotNull();
    }

    @Test
    void test_Delete() {
        //Initial Config.
        userRepository.deleteAll();

        //Arrange
        User defaultUser = new User(
                "default_id",
                "default_generatedKey",
                "default_uid",
                Instant.now()
        );
        userRepository.save(defaultUser);

        //Act
        userRepository.delete(defaultUser);

        //Assert
        Assertions.assertThat(userRepository.findByUid("default_uid")).isNull();
        assertThrows(IllegalArgumentException.class, () -> {
            userRepository.delete(null);
        });
    }
}
