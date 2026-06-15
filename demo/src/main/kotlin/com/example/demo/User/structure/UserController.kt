package com.example.demo.User.structure

import jakarta.servlet.http.HttpServletResponse
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
    fun createUser(@RequestBody request: UserRequest, response: HttpServletResponse
    ) : User? {

        if (!userService.doesUserExist(request.uid)) {
            response.status = HttpServletResponse.SC_CREATED

            return userService.createUser(
                uid = request.uid
            )
        }

        response.status = HttpServletResponse.SC_FOUND
        return null;
    }

    @GetMapping
    fun getUser(
        @RequestParam(required = true) uid: String, response: HttpServletResponse
    ) : UserResponse? {

        val returnedUser: Optional<User> = userService.getUser(uid)
        if (returnedUser.isPresent) {
            response.status = HttpServletResponse.SC_FOUND

            return UserResponse(
                generatedKey = returnedUser.get().generatedKey,
                timeCreated = returnedUser.get().timeCreated
            )
        }

        response.status = HttpServletResponse.SC_NOT_FOUND
        return null
    }

    @DeleteMapping
    fun removeUser(@RequestBody request: UserRequest) {
        userService.removeUser(request.uid)
    }
}
