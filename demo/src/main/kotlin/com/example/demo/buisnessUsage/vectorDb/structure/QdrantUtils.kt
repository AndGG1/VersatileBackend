package com.example.demo.buisnessUsage.vectorDb.structure

import com.example.demo.backendUsage.config.ShardManagement.ActiveQdrantConfig
import com.example.demo.backendUsage.config.WrappedHttpClient
import com.mongodb.annotations.Beta
import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.Duration
import java.util.regex.Pattern

data class QdrantClientConfig(
    val client: QdrantClient,
    val collectionName: String,
    val endpoint: String
)

@Component
class VectorClient(private val activeQdrantConfig: ActiveQdrantConfig) {
    fun getCurrentVectorClient() : QdrantClientConfig {
        val qdrantClient = activeQdrantConfig.getCurrentClient()

        return QdrantClientConfig(
            qdrantClient,
            "VersatilePTT_VectorCollection",
            "ENDPOINT_NOT_SHOWN"
        )
    }
}

@Configuration
class AllVectorClients() {
    @Value("\${qdrant.api.tokens}")
    private lateinit var apiKeys: String
    @Value("\${qdrant.cluster.urls}")
    private lateinit var clusterUrls: String

    @Value("\${shard.amount}")
    private lateinit var shardAmount: String

    @Bean
    fun getAllVectorClients() : List<QdrantClientConfig> {
        val listOfApis: List<String> = apiKeys.split(", ")
        val listOfUrls: List<String> = clusterUrls.split(", ")

        val listOfClientConfigs = ArrayList<QdrantClientConfig>()
        for (shard in 0..(shardAmount.toInt()-1)) {
            val clientConfig = QdrantClientConfig(
                QdrantClient(
                    QdrantGrpcClient.newBuilder(
                        listOfUrls[shard],
                        6334,
                        true
                    )
                        .withApiKey(listOfApis[shard])
                        .withTimeout(Duration.ofSeconds(10))
                        .build()
                ),
                "VersatilePTT_VectorCollection",
                listOfUrls[shard]
            )
            listOfClientConfigs.add(clientConfig)
        }

        return listOfClientConfigs
    }
}

//Informal comment: this function is in beta mode. It fails to follow fundamental rules that would have otherwise made it production-ready.
//TODO: Improve!
@Beta
fun isClusterStillAvailable(restClientBuilder: RestClient.Builder, apiKey: String, clusterUrl: String): Boolean {
    val formattedUrl = if (clusterUrl.startsWith("http://") || clusterUrl.startsWith("https://")) {
        clusterUrl
    } else {
        "https://$clusterUrl"
    }

    val client = WrappedHttpClient(
        restClientBuilder
            .baseUrl(formattedUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
            .build(),
        apiKey,
        formattedUrl
    )

    val softBlockDisk = 3_200_000_000L
    val softBlockRam = 750_000_000L

    val response: String? = client
        .client
        .get()
        .uri("/collections/VersatilePTT_VectorCollection/memory")
        .retrieve()
        .body(String::class.java)

    if (!response.isNullOrBlank()) {
        val diskMatcher = Pattern.compile("\"disk_bytes\":(?<memoryUsedInBytes>\\d+)").matcher(response)
        if (diskMatcher.find()) {
            val memoryUsedDisk = diskMatcher.group("memoryUsedInBytes").toLongOrNull() ?: 0L
            if (memoryUsedDisk > softBlockDisk) return false
        }

        val ramMatcher = Pattern.compile("\"ram\":(?<memoryUsedInBytes>\\d+)").matcher(response)
        if (ramMatcher.find()) {
            val memoryUsedRam = ramMatcher.group("memoryUsedInBytes").toLongOrNull() ?: 0L
            if (memoryUsedRam > softBlockRam) return false
        }
    }

    return true
}