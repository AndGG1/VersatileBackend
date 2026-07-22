package com.example.demo.vectorDb.structure

import io.qdrant.client.ConditionFactory.matchKeyword
import io.qdrant.client.PointIdFactory.id
import io.qdrant.client.ValueFactory.value
import io.qdrant.client.WithPayloadSelectorFactory.enable
import io.qdrant.client.grpc.Points
import kotlinx.coroutines.guava.await
import org.springframework.stereotype.Repository

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
}
