package com.example.demo.buisnessUsage.vectorDb.structure

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
        "023d1e24-cb13-4159-a79f-51abf3f8ee0d.europe-west3-0.gcp.cloud.qdrant.io"

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