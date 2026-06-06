package com.example.demo.User.structure

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,

    val generatedKey: String = UUID.randomUUID().toString(),
    val uid: String,
    val timeCreated: Instant = Instant.now()
)