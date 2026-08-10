package com.example.demo.buisnessUsage.vectorDb.structure

import com.example.demo.backendUsage.config.ActiveQdrantConfig
import io.qdrant.client.QdrantClient
import org.springframework.stereotype.Component

data class QdrantClientConfig(
    val client: QdrantClient,
    val collectionName: String
)

@Component
class VectorClient(private val activeQdrantConfig: ActiveQdrantConfig) {
    fun getVectorClient() : QdrantClientConfig {
        val qdrantClient = activeQdrantConfig.getCurrentClient()
        println(activeQdrantConfig.getCurrentClient())

        return QdrantClientConfig(
            qdrantClient,
            "VersatilePTT_VectorCollection"
        )
    }
}