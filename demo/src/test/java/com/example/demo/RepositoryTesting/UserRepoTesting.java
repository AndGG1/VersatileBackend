package com.example.demo.RepositoryTesting;

import com.example.demo.buisnessUsage.users.structure.User;
import com.example.demo.buisnessUsage.users.structure.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.Instant;

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@DataMongoTest
public class UserRepoTesting {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void test_FindById() {
        //Arrange
        String id = "test_uid";
        User newUser = new User(
                "test_id",
                "test_generatedKey",
                "test_uid",
                Instant.now()
        );

        //Act
        userRepository.save(newUser);

        //Assert
        var res = userRepository.findByUid(id);
        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.getId()).isEqualTo(id);
        Assertions.assertThat(res.getGeneratedKey()).isEqualTo(newUser.getGeneratedKey());
        Assertions.assertThat(res.getUid()).isEqualTo(newUser.getUid());
    }

    @Test
    public void test_Save() {
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
        Assertions.assertThat(res.getId()).isEqualTo(newUser.getId());
        Assertions.assertThat(res.getGeneratedKey()).isEqualTo(newUser.getGeneratedKey());
        Assertions.assertThat(res.getUid()).isEqualTo(newUser.getUid());
    }

    @Test
    void test_Delete() {
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
    }
}