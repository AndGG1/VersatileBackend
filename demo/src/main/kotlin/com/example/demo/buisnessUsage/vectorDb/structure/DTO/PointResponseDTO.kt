package com.example.demo.buisnessUsage.vectorDb.structure.DTO

data class PointResponseDto(
    val productName: String,
    val vector: List<Float>
)