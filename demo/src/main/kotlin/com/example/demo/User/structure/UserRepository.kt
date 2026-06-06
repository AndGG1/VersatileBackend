package com.example.demo.User.structure

import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findByUid(uid: String): User?
}