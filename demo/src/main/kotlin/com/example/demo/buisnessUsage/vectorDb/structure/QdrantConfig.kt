package com.example.demo.buisnessUsage.vectorDb.structure

import com.example.demo.backendUsage.config.ActiveQdrantConfig
import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.time.Duration

data class QdrantClientConfig(
    val client: QdrantClient,
    val collectionName: String
)

@Component
class VectorClient(private val activeQdrantConfig: ActiveQdrantConfig) {
    fun getCurrentVectorClient() : QdrantClientConfig {
        val qdrantClient = activeQdrantConfig.getCurrentClient()

        return QdrantClientConfig(
            qdrantClient,
            "VersatilePTT_VectorCollection"
        )
    }
}

//TODO: fix
@Configuration
class AllVectorClients() {
    @Value("\${qdrant.api.tokens}")
    private lateinit var apiKeys: String

    @Value("\${qdrant.cluster.urls}")
    private lateinit var clusterUrls: String

    @Bean
    fun getAllVectorClients() : List<QdrantClientConfig> {
        val listOfApis: List<String> = apiKeys.split(", ")
        val listOfUrls: List<String> = clusterUrls.split(", ")

        val listOfClientConfigs = ArrayList<QdrantClientConfig>()
      //  for (i in 0..2) {
            val clientConfig = QdrantClientConfig(
                QdrantClient(
                    QdrantGrpcClient.newBuilder(
                        listOfUrls[0],
                        6334,
                        true)
                        .withApiKey(listOfApis[0])
                        .withTimeout(Duration.ofSeconds(10))
                        .build()
                ),
                "VersatilePTT_VectorCollection"
            )

            listOfClientConfigs.add(clientConfig)
      //  }

        return listOfClientConfigs
    }
}