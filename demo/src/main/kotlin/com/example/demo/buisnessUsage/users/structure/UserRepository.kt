package com.example.demo.buisnessUsage.users.structure

import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findByUid(uid: String): User?
}