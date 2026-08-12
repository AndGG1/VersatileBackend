package com.example.demo.backendUsage.config

import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ActiveQdrantConfig {
    @Volatile
    private var currentUrl: String? = null

    @Volatile
    private var currentApi: String? = null
    @Volatile
    private var currQdrantClient: QdrantClient? = null


    fun getCurrentClient() : QdrantClient {
        if (currQdrantClient == null) {
            throw NullPointerException()
        }
        return currQdrantClient!!
    }

    @Synchronized
    fun upsertCurrentClient(newUrl: String, newApi: String) {
        val tempClient = currQdrantClient
        if (currQdrantClient == null || newUrl != currentUrl || newApi != currentApi) {
            currQdrantClient = QdrantClient(
                QdrantGrpcClient.newBuilder(
                    newUrl,
                    6334,
                    true)
                    .withApiKey(newApi)
                    .withTimeout(Duration.ofSeconds(10))
                    .build()
            )
            currentUrl = newUrl
            currentApi = newApi

            tempClient?.close()
        }
    }
}