package com.example.demo.buisnessUsage.users.structure.DTO

import jakarta.validation.constraints.NotNull
import java.time.Instant

data class UserDTO(
    @field:NotNull val uid: String,
    @field:NotNull val timeCreated: Instant
)