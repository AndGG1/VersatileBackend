package com.example.demo.buisnessUsage.users.structure.DTO;

import com.example.demo.buisnessUsage.users.structure.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.function.Function;

@Service
public class UserDTOMapper implements Function<User, UserDTO> {

    @Override
    public UserDTO apply(User user) {
        if (user == null) {
            user = new User(
                    "null",
                    "null",
                    Instant.now()
            );
        }

        return new UserDTO(
                user.getUid(),
                user.getTimeCreated()
        );
    }
}
