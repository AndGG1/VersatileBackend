package com.example.demo.buisnessUsage.users.structure.DTO;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UserDTO(
        @NotNull String uid,
        @NotNull Instant timeCreated
) {}