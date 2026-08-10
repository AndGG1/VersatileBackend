package com.example.demo.backendUsage.config

import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import org.springframework.stereotype.Component

@Component
class ActiveQdrantConfig {
    @Volatile
    private var currentUrl: String? = null

    @Volatile
    private var currentApi: String? = null
    @Volatile
    private var currQdrantClient: QdrantClient? = null


    fun getCurrentClient() : QdrantClient {
        println(currentUrl)
        println(currentApi)
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
                    .build()
            )
            currentUrl = newUrl
            currentApi = newApi

            tempClient?.close()
        }
    }
}