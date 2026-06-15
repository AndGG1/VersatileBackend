package com.example.demo.User.structure

import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserService(private val userRepository: UserRepository) {

    fun createUser(uid: String): User {
        return userRepository.save(User(uid = uid))
    }

    fun doesUserExist(uid: String): Boolean {
        return userRepository.findByUid(uid) != null
    }

    fun getUser(uid: String): Optional<User> {
        return Optional.ofNullable(userRepository.findByUid(uid))
    }

    fun removeUser(uid: String) {
        userRepository.delete(getUser(uid).get())
    }
}