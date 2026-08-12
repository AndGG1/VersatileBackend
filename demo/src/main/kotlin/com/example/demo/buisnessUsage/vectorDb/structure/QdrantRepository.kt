package com.example.demo.buisnessUsage.vectorDb.structure

import io.qdrant.client.ConditionFactory.matchKeyword
import io.qdrant.client.PointIdFactory.id
import io.qdrant.client.ValueFactory.value
import io.qdrant.client.WithPayloadSelectorFactory.enable
import io.qdrant.client.grpc.Points
import kotlinx.coroutines.guava.await
import org.springframework.stereotype.Repository

@Repository
class QdrantRepository(private val vectorClient: VectorClient,
    private val getAllVectorClients: List<QdrantClientConfig>) {

    fun save(pointRequest: QdrantPointRequest) {
        val getVectorClient = vectorClient.getCurrentVectorClient()

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


    suspend fun getAll(uid: String): List<Points.ScrollResponse?> {
        val res = getAllVectorClients.map { clientConfig ->
            clientConfig.client
                .scrollAsync(
                    Points.ScrollPoints.newBuilder()
                        .setCollectionName(clientConfig.collectionName)
                        .setFilter(
                            Points.Filter.newBuilder()
                                .addMust(matchKeyword("uid", uid))
                                .build()
                        )
                        .setLimit(1)
                        .setWithPayload(enable(true))
                        .build()
                )
        }

        return res.map { it.await() }
    }

    suspend fun getOneOrMore(payloadRequest: CollectionPayload) : List<Points.ScrollResponse?> {
        val res = getAllVectorClients.map { clientConfig ->
            clientConfig.client
                .scrollAsync(
                    Points.ScrollPoints.newBuilder()
                        .setCollectionName(clientConfig.collectionName)
                        .setFilter(
                            Points.Filter.newBuilder()
                                .addMust(matchKeyword("uid", payloadRequest.uid))
                                .addMust(matchKeyword("product_name", payloadRequest.productName))
                                .build()
                        )
                        .setLimit(1)
                        .setWithPayload(enable(true))
                        .build()
                )
        }

        return res.map { it.await()}
    }

    suspend fun deleteAll(uid: String): List<Points.UpdateResult?> {
        val res = getAllVectorClients.map { clientConfig ->
            clientConfig.client.deleteAsync(
                clientConfig.collectionName,
            Points.Filter.newBuilder()
                .addMust(matchKeyword("uid", uid))
                .build()
            )
        }

        return res.map { it.await() }
    }
}
