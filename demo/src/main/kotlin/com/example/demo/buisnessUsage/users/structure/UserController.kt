package com.example.demo.buisnessUsage.users.structure

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.Optional

data class UserRequest(val uid: String)
data class UserResponse(val generatedKey: String, val timeCreated: Instant)

@RestController
@RequestMapping("/versatile_api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun createUser(@RequestBody request: UserRequest) : User? {
        val user = userService.createUser(
            uid = request.uid
        )

        return user
    }

    @GetMapping
    fun getUser(@RequestParam(required = true) uid: String): UserResponse? {
        val returnedUser: Optional<User> = userService.getUser(uid)

        return UserResponse(
            generatedKey = returnedUser.get().generatedKey,
            timeCreated = returnedUser.get().timeCreated
        )
    }

    @DeleteMapping
    fun removeUser(@RequestBody request: UserRequest) {
        userService.removeUser(request.uid)
    }
}
