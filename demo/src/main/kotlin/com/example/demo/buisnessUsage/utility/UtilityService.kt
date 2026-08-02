package com.example.demo.buisnessUsage.utility

import com.example.demo.buisnessUsage.embeddings.structure.EmbeddingService
import io.qdrant.client.QueryFactory.nearest
import io.qdrant.client.grpc.Collections
import io.qdrant.client.grpc.Points
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException

//TODO: Redo!!!
@Service
class UtilityService(private val embeddingService: EmbeddingService, private val config: GetCollectionConfig, private val clientGenerator: QdrantClientGenerator) {

    fun findSimilarVectors(semRepresentation: String, limit: Int, collectionName: String): List<String> {
        val collectionInfo = try {
            config.getCollection()?.get()
                ?: throw IllegalStateException("Collection configuration returned null.")
        } catch (e: ExecutionException) {
            throw IllegalStateException("Collection '$collectionName' is not active or could not be reached!", e)
        }

        if (collectionInfo.status == Collections.CollectionStatus.Red) {
            throw IllegalStateException("Collection '$collectionName' is in a RED/Degraded state.")
        }

        val client = clientGenerator.getClient()
        val vector = embeddingService.embed(listOf(semRepresentation))
        val mappedVector = vector.embeddings[0].map { it.toFloat() }

        val queryResponse = client.queryAsync(Points.QueryPoints.newBuilder()
            .setCollectionName(collectionName)
            .setQuery(nearest(mappedVector))
            .setLimit(limit.toLong())
            .setWithPayload(Points.WithPayloadSelector.newBuilder().setEnable(true).build())
            .build()
        ).get()

        return queryResponse.map {point ->
            point.getPayloadOrThrow("product_name").stringValue
        }
    }
}
