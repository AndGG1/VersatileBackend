package com.example.demo.vectorDb.structure

import org.springframework.stereotype.Repository
import org.springframework.web.client.RestClient

@Repository
class QdrantRepository(private val getVectorClient: RestClient) {

    fun save(pointRequest: QdrantPointRequest) {
        val requestBody = mapOf("points" to listOf(pointRequest))

        getVectorClient
            .put()
            .uri("/points?wait=true")
            .body(requestBody)
            .retrieve()
            .toBodilessEntity()
    }
}