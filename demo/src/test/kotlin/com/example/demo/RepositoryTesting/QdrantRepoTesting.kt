package com.example.demo.RepositoryTesting;

import com.example.demo.buisnessUsage.vectorDb.structure.CollectionPayload
import com.example.demo.buisnessUsage.vectorDb.structure.QdrantClientConfig
import com.example.demo.buisnessUsage.vectorDb.structure.QdrantPointRequest
import com.example.demo.buisnessUsage.vectorDb.structure.QdrantRepository
import com.example.demo.buisnessUsage.vectorDb.structure.VectorClient
import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import io.qdrant.client.grpc.Points
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.qdrant.QdrantContainer
import java.util.UUID
import kotlin.test.Test

@Testcontainers
@ExtendWith(MockitoExtension::class)
class QdrantRepoTesting {

    @Container
    private val qdrant = QdrantContainer("qdrant/qdrant:latest")

    private lateinit var qdrantRepository: QdrantRepository

    @Mock
    private lateinit var vectorClient: VectorClient

    private val COLLECTION_NAME = "test_collection"

    @BeforeEach
    fun setUp() {
        val qdrantClient = QdrantClient(
            QdrantGrpcClient.newBuilder(
                qdrant.host,
                qdrant.grpcPort,
                false
            ).build()
        )

        if (!qdrantClient.collectionExistsAsync(COLLECTION_NAME).get()) {
            qdrantClient.createCollectionAsync(
                COLLECTION_NAME,
                io.qdrant.client.grpc.Collections.VectorParams.newBuilder()
                    .setSize(3)
                    .setDistance(io.qdrant.client.grpc.Collections.Distance.Dot)
                    .build()
            ).get()
        }

        qdrantClient.deleteAsync(COLLECTION_NAME, Points.Filter.getDefaultInstance()).get()

        val config = QdrantClientConfig(qdrantClient, COLLECTION_NAME)
        Mockito.`when`<QdrantClientConfig?>(vectorClient.getCurrentVectorClient()).thenReturn(config)

        qdrantRepository = QdrantRepository(vectorClient, listOf(config))
    }

    @Test
    fun test_Save() {
        runBlocking {
            // Arrange
            val uid = "test_user_save"
            val request = QdrantPointRequest(
                UUID.randomUUID(),
                listOf(0.1f, 0.2f, 0.3f),
                CollectionPayload(uid, "test_product_keyboard")
            )

            // Act
            val res = qdrantRepository.save(request)

            // Assert
            assertThat(res).isNotNull
            assertThat(res?.status).isEqualTo(Points.UpdateStatus.Completed)
        }
    }

        @Test
        fun test_GetOneOrMore() {
            runBlocking {
                // Arrange
                val payload = CollectionPayload("user_filter_uid", "laptop")
                val request = QdrantPointRequest(
                    UUID.randomUUID(),
                    listOf(0.5f, 0.5f, 0.5f),
                    payload
                )

                // Save data first
                qdrantRepository.save(request)

                // Act
                val results = qdrantRepository.getOneOrMore(payload)

                // Assert - Validates filter matching uid AND product_name
                assertThat(results).isNotEmpty
                assertThat(results[0]?.resultList).hasSize(1)
                assertThat(results[0]?.getResult(0)?.containsPayload("product_name")).isTrue
            }
        }

        @Test
        fun test_DeleteAll() {
            runBlocking {
                // Arrange
                val uidToDelete = "user_to_delete"
                val request = QdrantPointRequest(
                    UUID.randomUUID(),
                    listOf(0.0f, 0.9f, 0.1f),
                    CollectionPayload(uidToDelete, "item_to_remove")
                )

                // Save data first
                qdrantRepository.save(request)

                // Act
                qdrantRepository.deleteAll(uidToDelete)

                // Assert - Perform getAll to verify records were deleted
                val results = qdrantRepository.getAll(uidToDelete)

                assertThat(results).isNotEmpty
                assertThat(results[0]?.resultList).isEmpty()
            }
        }
}