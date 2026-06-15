package com.example.demo.utility

import com.google.common.util.concurrent.ListenableFuture
import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import io.qdrant.client.grpc.Collections
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class GetCollectionConfig(private val clientGenerator: QdrantClientGenerator) {

    @Bean
    fun getCollection(): ListenableFuture<Collections.CollectionInfo?>? {
        val client = clientGenerator.getClient()
        return client.getCollectionInfoAsync("VersatilePTT_VectorCollection")
    }
}

@Component
class QdrantClientGenerator(@Value("\${qdrant.api.token}") private val apiKey: String) {
    fun getClient(): QdrantClient {
        return QdrantClient(
            QdrantGrpcClient.newBuilder(
                "023d1e24-cb13-4159-a79f-51abf3f8ee0d.europe-west3-0.gcp.cloud.qdrant.io",
                6334,
                true
            )
                .withApiKey(apiKey)
                .build()
        )
    }
}