package com.example.demo.User.structure

import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserService(private val userRepository: UserRepository) {

    fun createUser(uid: String): User? {
        if (uid.isNotEmpty() && uid.isNotBlank()) {
            return userRepository.save(User(uid = uid))

        } else return null
    }

    fun doesUserExist(uid: String): Boolean {
        return userRepository.findByUid(uid) != null
    }

    fun getUser(uid: String): Optional<User> {
        return Optional.ofNullable(userRepository.findByUid(uid))
    }

    fun removeUser(uid: String) {
        val optionalUser: Optional<User> = getUser(uid)

        if (optionalUser.isPresent) {
            userRepository.delete(optionalUser.get())
        }
    }
}