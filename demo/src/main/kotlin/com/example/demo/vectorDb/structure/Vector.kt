package com.example.demo.vectorDb.structure

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class QdrantPointRequest(
    val id: String = UUID.randomUUID().toString(),
    val vector: List<Float>,
    val payload: CollectionPayload
)

data class CollectionPayload(
    val uid: String,

    @JsonProperty("product_name")
    val productName: String
)