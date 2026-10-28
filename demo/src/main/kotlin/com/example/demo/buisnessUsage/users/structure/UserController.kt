package com.example.demo.buisnessUsage.users.structure

import com.example.demo.backendUsage.config.RateLimiting.UserRateLimiting.UserReadRateLimiterConfig
import com.example.demo.backendUsage.config.RateLimiting.UserRateLimiting.UserWriteRateLimiterConfig
import com.example.demo.buisnessUsage.users.structure.DTO.UserDTO
import com.example.demo.buisnessUsage.users.structure.DTO.UserDTOMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

data class UserRequest(val uid: String)

@RestController
@RequestMapping("/versatile_api/users")
class UserController(private val userService: UserService,
    private val httpServlet: HttpServletResponse,
    private val userWriteRateLimiterConfig: UserWriteRateLimiterConfig,
    private val userReadRateLimiterConfig: UserReadRateLimiterConfig,
    private val userDTOMapper: UserDTOMapper) {

    @PostMapping
    fun createUser(@RequestBody request: UserRequest) : UserDTO {
        userWriteRateLimiterConfig.getUserCustomWriteRateLimiter().acquire()

        val user = userService.createUser(
            uid = request.uid
        )
        if (user == null) {
            httpServlet.status = HttpServletResponse.SC_CONFLICT
        } else {
            httpServlet.status = HttpServletResponse.SC_CREATED
        }

        return userDTOMapper.apply(user ?: User("null", "null", Instant.now()))
    }

    @GetMapping
    fun getAllUsers() : List<UserDTO> {
        userReadRateLimiterConfig.getUserCustomReadRateLimiter().tryAcquire()

        return userService.getAllUsers()
            .stream()
            .map {
                user -> userDTOMapper.apply(user ?: User("null", "null", Instant.now()))
            }
            .toList()
    }

    @DeleteMapping
    fun removeUser(@RequestBody request: UserRequest) {
        userReadRateLimiterConfig.getUserCustomReadRateLimiter().tryAcquire()

        userService.removeUser(request.uid)
    }
}