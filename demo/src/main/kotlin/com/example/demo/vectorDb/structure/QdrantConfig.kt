package com.example.demo.vectorDb.structure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class VectorClient {
    private val client = RestClient.builder()

    @Value("\${qdrant.api.token}")
    private lateinit var apiKey: String
    private val VECTOR_DB_ENDPOINT = "https://023d1e24-cb13-4159-a79f-51abf3f8ee0d.europe-west3-0.gcp.cloud.qdrant.io:6334/collections/VersatilePTT_VectorCollection"

    @Bean
    fun getVectorClient() : RestClient {
        return client
            .baseUrl(VECTOR_DB_ENDPOINT)
            .defaultHeader("Authorization", "Bearer $apiKey")
            .defaultHeader("Content-Type", "application/json")
            .build()
    }
}