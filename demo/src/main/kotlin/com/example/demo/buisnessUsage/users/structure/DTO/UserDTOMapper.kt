package com.example.demo.buisnessUsage.users.structure.DTO

import com.example.demo.buisnessUsage.users.structure.User
import org.springframework.stereotype.Service
import java.util.function.Function

@Service
class UserDTOMapper : Function<User, UserDTO> {

    override fun apply(t: User): UserDTO {
        val resolvedUser = t

        return UserDTO(
            uid = resolvedUser.uid,
            timeCreated = resolvedUser.timeCreated
        )
    }

}