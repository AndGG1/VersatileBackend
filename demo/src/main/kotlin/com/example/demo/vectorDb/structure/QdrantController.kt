package com.example.demo.vectorDb.structure

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ProductUploadRequest(
    val vector: List<Float>,
    val uid: String,
    val productName: String
)

@RestController
@RequestMapping("/versatile_api/vector_database")
class QdrantController(private val service: QdrantService) {

    @PostMapping
    fun upsertPoint(@RequestBody requestBody: ProductUploadRequest) {
        val payload = CollectionPayload(requestBody.uid, requestBody.productName)
        val qdrantRequest = QdrantPointRequest(vector = requestBody.vector, payload = payload)

        service.upsertPoint(qdrantRequest)
    }
}