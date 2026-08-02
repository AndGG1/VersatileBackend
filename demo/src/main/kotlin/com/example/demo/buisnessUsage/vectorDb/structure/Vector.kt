package com.example.demo.buisnessUsage.vectorDb.structure

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class QdrantPointRequest(
    @JsonProperty("id")
    val id: UUID = UUID.randomUUID(),

    val vector: List<Float>,
    val payload: CollectionPayload
)

data class CollectionPayload(
    @JsonProperty("uid")
    val uid: String,

    @JsonProperty("product_name")
    val productName: String,
)