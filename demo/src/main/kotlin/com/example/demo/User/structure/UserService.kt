package com.example.demo.User.structure

import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun createUser(username: String, uid: String) : User {
        val user: User = User(username = username, uid = uid)

        return userRepository.save(user)
    }
}