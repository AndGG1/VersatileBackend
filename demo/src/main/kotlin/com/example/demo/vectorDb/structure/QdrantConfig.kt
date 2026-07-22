package com.example.demo.vectorDb.structure

import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

data class QdrantClientConfig(
    val client: QdrantClient,
    val collectionName: String
)

@Configuration
class VectorClient {

    @Value("\${qdrant.api.token}")
    private lateinit var apiKey: String

    private val VECTOR_DB_HOST =
        "e1dcd2d7-54df-4316-b09b-27b96d095397.eu-central-1-0.aws.cloud.qdrant.io"

    @Bean
    fun getVectorClient() : QdrantClientConfig {
        val qdrantClient = QdrantClient(
            QdrantGrpcClient.newBuilder(
                VECTOR_DB_HOST,
                6334,
                true)
                .withApiKey(apiKey)
                .build()
        )

        return QdrantClientConfig(
            qdrantClient,
            "VersatilePTT_VectorCollection"
        )
    }
}