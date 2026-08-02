package com.example.demo.buisnessUsage.utility

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
                "e1dcd2d7-54df-4316-b09b-27b96d095397.eu-central-1-0.aws.cloud.qdrant.io",
                6334,
                true
            )
                .withApiKey(apiKey)
                .build()
        )
    }
}