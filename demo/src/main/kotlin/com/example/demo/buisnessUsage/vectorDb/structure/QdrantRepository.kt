package com.example.demo.buisnessUsage.vectorDb.structure

import com.example.demo.backendUsage.config.WrappedHttpClient
import com.google.common.net.HttpHeaders
import io.qdrant.client.ConditionFactory.matchKeyword
import io.qdrant.client.PointIdFactory.id
import io.qdrant.client.ValueFactory.value
import io.qdrant.client.WithPayloadSelectorFactory.enable
import io.qdrant.client.grpc.Points
import kotlinx.coroutines.guava.await
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestClient
import java.util.regex.Pattern

@Repository
class QdrantRepository(private val getVectorClient: QdrantClientConfig) {

    fun save(pointRequest: QdrantPointRequest) {
        val vectorUnit = Points.Vectors.newBuilder()
            .setVector(
                Points.Vector.newBuilder()
                    .addAllData(pointRequest.vector)
                    .build()
            )
            .build()

        val payloadKeys = mapOf(
            "uid" to value(pointRequest.payload.uid),
            "product_name" to value(pointRequest.payload.productName)
        )

        getVectorClient.client.upsertAsync(
            getVectorClient.collectionName,
            listOf(
            Points.PointStruct.newBuilder()
                .setId(id(pointRequest.id))
                .setVectors(vectorUnit)
                .putAllPayload(payloadKeys)
                .build()
        ))
    }


    suspend fun getAll(uid: String): Points.ScrollResponse? {

        return getVectorClient.client
            .scrollAsync(
                Points.ScrollPoints.newBuilder()
                    .setCollectionName(getVectorClient.collectionName)
                    .setFilter(
                        Points.Filter.newBuilder()
                            .addMust(matchKeyword("uid", uid))
                            .build()
                    )
                    .setLimit(1)
                    .setWithPayload(enable(true))
                    .build())
            .await()
    }

    suspend fun getOneOrMore(payloadRequest: CollectionPayload) : Points.ScrollResponse? {

        return getVectorClient.client
            .scrollAsync(
                Points.ScrollPoints.newBuilder()
                    .setCollectionName(getVectorClient.collectionName)
                    .setFilter(
                        Points.Filter.newBuilder()
                            .addMust(matchKeyword("uid", payloadRequest.uid))
                            .addMust(matchKeyword("product_name", payloadRequest.productName))
                            .build()
                    )
                    .setLimit(1)
                    .setWithPayload(enable(true))
                    .build())
            .await()
    }

    suspend fun deleteAll(uid: String): Points.UpdateResult? {

        return getVectorClient.client.deleteAsync(
            getVectorClient.collectionName,
            Points.Filter.newBuilder()
                .addMust(matchKeyword("uid", uid))
                .build()
        ).await()
    }

    fun isClusterStillAvailable(getHttpClientConfig: WrappedHttpClient): Boolean {
        val clusterUrl = getHttpClientConfig.clusterUrl
        val apiKey = getHttpClientConfig.apiKey

        val client = WrappedHttpClient(
            RestClient.builder()
                .baseUrl(clusterUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
                .build(),
            apiKey,
            clusterUrl
        )

        val softBlockDisk = 3_200_000_000; val softBlockRam = 750_000_000
        val response: String? = client
            .client
            .get()
            .uri("/collections/VersatilePTT_VectorCollection/memory")
            .retrieve()
            .body(String::class.java)

        if (response != null && !response.isBlank()) {
            var matcher =
                Pattern.compile("\"disk_bytes\":(?<memoryUsedInBytes>\\d+)").matcher(response)
            matcher.find()
            val memoryUsedDisk: String = matcher.group("memoryUsedInBytes")
            var convertedMemoryUsedToBytes: Long = memoryUsedDisk.toLong()

            if (convertedMemoryUsedToBytes > softBlockDisk) return false


            matcher = Pattern.compile("\"ram\":(?<memoryUsedInBytes>\\d+)").matcher(response)
            matcher.find()
            val memoryUsedRam: String = matcher.group("memoryUsedInBytes")
            convertedMemoryUsedToBytes = memoryUsedRam.toLong()

            if (convertedMemoryUsedToBytes > softBlockRam) return false
        }

        return true
    }
}
