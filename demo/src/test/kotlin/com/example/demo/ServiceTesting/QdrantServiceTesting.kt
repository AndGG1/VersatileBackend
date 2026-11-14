package com.example.demo.ServiceTesting

import com.example.demo.backendUsage.health.NotifierService
import com.example.demo.buisnessUsage.vectorDb.structure.*
import io.qdrant.client.grpc.Points
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@ExtendWith(MockitoExtension::class)
class QdrantServiceTesting {

    @Mock
    private lateinit var repository: QdrantRepository

    @Mock
    private lateinit var notifierService: NotifierService

    @InjectMocks
    private lateinit var qdrantService: QdrantService

    private val validTestUid = "2F2RMuYb+/IyaNJ-fOKOhWqO_sPv"

    @Test
    fun test_UpsertPoint_Success() {
        runBlocking {
            // Arrange
            val request = QdrantPointRequest(
                UUID.randomUUID(),
                listOf(0.1f, 0.2f, 0.3f),
                CollectionPayload(validTestUid, "keyboard")
            )
            val mockUpdateResult = Points.UpdateResult.getDefaultInstance()
            whenever(repository.save(any())).thenReturn(mockUpdateResult)

            // Act
            val result = qdrantService.upsertPoint(request)

            // Assert
            assertThat(result).isNotNull()
            verify(repository, times(1)).save(request)
        }
    }

    @Test
    fun test_UpsertPoint_Fail_InvalidUid() {
        runBlocking {
            // Arrange
            val request = QdrantPointRequest(
                UUID.randomUUID(),
                listOf(0.1f, 0.2f, 0.3f),
                CollectionPayload("invalid_short_uid", "keyboard")
            )

            // Act & Assert
            org.junit.jupiter.api.assertThrows<Exception> {
                qdrantService.upsertPoint(request)
            }
        }
    }

    @Test
    fun test_GetAllPointsByUid_Success() {
        runBlocking {
            // Arrange
            val scrollResponse = Points.ScrollResponse.getDefaultInstance()
            whenever(repository.getAll(validTestUid)).thenReturn(listOf(scrollResponse))

            // Act
            val results = qdrantService.getAllPointsByUid(validTestUid)

            // Assert
            assertThat(results).isNotEmpty()
            assertThat(results).hasSize(1)
            verify(repository, times(1)).getAll(validTestUid)
        }
    }

    @Test
    fun test_GetAllPointsByUid_Fail_Empty() {
        runBlocking {
            // Arrange
            Mockito.`when`(repository.getAll(validTestUid)).thenReturn(emptyList())

            // Act & Assert
            org.junit.jupiter.api.assertThrows<NullPointerException> {
                qdrantService.getAllPointsByUid(validTestUid)
            }
        }
    }

    @Test
    fun test_GetPointsByPayloads_Success() {
        runBlocking {
            // Arrange
            val payload = CollectionPayload(validTestUid, "laptop")
            val scrollResponse = Points.ScrollResponse.getDefaultInstance()
            Mockito.`when`(repository.getOneOrMore(payload)).thenReturn(listOf(scrollResponse))

            // Act
            val results = qdrantService.getPointsByPayloads(payload)

            // Assert
            assertThat(results).isNotEmpty()
            Mockito.verify(repository, Mockito.times(1)).getOneOrMore(payload)
        }
    }

    @Test
    fun test_DeleteAllPointsByUid_Success() {
        runBlocking {
            // Arrange
            val updateResult = Points.UpdateResult.getDefaultInstance()
            Mockito.`when`(repository.deleteAll(validTestUid)).thenReturn(listOf(updateResult))

            // Act
            val results = qdrantService.deleteAllPointsByUid(validTestUid)

            // Assert
            assertThat(results).isNotEmpty()
            Mockito.verify(repository, Mockito.times(1)).deleteAll(validTestUid)
        }
    }
}