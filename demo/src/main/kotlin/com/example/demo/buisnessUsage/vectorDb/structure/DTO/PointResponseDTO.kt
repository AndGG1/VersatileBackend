package com.example.demo.buisnessUsage.vectorDb.structure.DTO

import io.qdrant.client.grpc.Points

data class PointResponseDto(
    val productName: String,
    val vector: List<Float>
) {
    constructor() : this("test_product", listOf(1.2f, 2.3f, 3.4f))
}

data class UpdatedPointResponseDTO(
    val status: String
) {
    constructor() : this("COMPLETED")
}