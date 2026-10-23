package com.example.demo.buisnessUsage.users.structure

import com.example.demo.backendUsage.config.RateLimiting.UserRateLimiting.UserReadRateLimiterConfig
import com.example.demo.backendUsage.config.RateLimiting.UserRateLimiting.UserWriteRateLimiterConfig
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
class UserController(private val userService: UserService,
    private val httpServlet: HttpServletResponse,
    private val userWriteRateLimiterConfig: UserWriteRateLimiterConfig,
    private val userReadRateLimiterConfig: UserReadRateLimiterConfig) {

    @PostMapping
    fun createUser(@RequestBody request: UserRequest) : User? {
        userWriteRateLimiterConfig.getUserCustomWriteRateLimiter().acquire()

        val user = userService.createUser(
            uid = request.uid
        )

        if (user == null) {
            httpServlet.status = HttpServletResponse.SC_CONFLICT
        } else {
            httpServlet.status = HttpServletResponse.SC_CREATED
        }
        return user
    }

    @GetMapping
    fun getUser(@RequestParam(required = true) uid: String): UserResponse? {
        userReadRateLimiterConfig.getUserCustomReadRateLimiter().tryAcquire()

        val returnedUser: Optional<User> = userService.getUser(uid)
        if (returnedUser.isEmpty) {
            httpServlet.status = HttpServletResponse.SC_NOT_FOUND
            return null
        }

        httpServlet.status = HttpServletResponse.SC_FOUND
        return UserResponse(
            generatedKey = returnedUser.get().generatedKey,
            timeCreated = returnedUser.get().timeCreated
        )
    }

    @DeleteMapping
    fun removeUser(@RequestBody request: UserRequest) {
        userReadRateLimiterConfig.getUserCustomReadRateLimiter().tryAcquire()

        userService.removeUser(request.uid)
    }
}