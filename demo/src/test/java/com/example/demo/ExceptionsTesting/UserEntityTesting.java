package com.example.demo.ExceptionsTesting;

import com.example.demo.User.structure.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserEntityTesting {

    @Test
    public void test_UserNonNull() {
        assertThrows(NullPointerException.class, () -> {
            new User(
                    null,
                    null,
                    null,
                    null
            );
        });
    }
}
