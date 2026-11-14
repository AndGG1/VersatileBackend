package com.example.demo.ControllerTesting

import com.example.demo.backendUsage.config.RateLimiting.QdrantRateLimiting.QdrantReadRateLimiterConfig
import com.example.demo.backendUsage.config.RateLimiting.QdrantWriteRateLimiterConfig.QdrantWriteRateLimiterConfig
import com.example.demo.buisnessUsage.vectorDb.structure.*
import com.example.demo.buisnessUsage.vectorDb.structure.DTO.PointResponseDTOMapper
import com.example.demo.buisnessUsage.vectorDb.structure.DTO.PointResponseDto
import com.example.demo.buisnessUsage.vectorDb.structure.DTO.UpdatedPointResponseDTO
import com.example.demo.buisnessUsage.vectorDb.structure.DTO.UpdatedPointResponseDTOMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.util.concurrent.RateLimiter
import io.qdrant.client.grpc.Points
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

//Informative Comment: For testing the Model: AAA("Triple A") is used. Any test should be rooting for this Model.

@WebMvcTest(controllers = [QdrantController::class])
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension::class)
class QdrantControllerTesting {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var qdrantService: QdrantService

    @MockitoBean
    private lateinit var qdrantReadRateLimiterConfig: QdrantReadRateLimiterConfig

    @MockitoBean
    private lateinit var qdrantWriteRateLimiterConfig: QdrantWriteRateLimiterConfig

    @MockitoBean
    private lateinit var pointResponseDTOMapper: PointResponseDTOMapper

    @MockitoBean
    private lateinit var updatedPointResponseDTOMapper: UpdatedPointResponseDTOMapper

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val validTestUid = "2F2RMuYb+/IyaNJ-fOKOhWqO_sPv"

    @BeforeEach
    fun setUpRateLimiters() {
        val dummyLimiter = RateLimiter.create(100.0)
        whenever(qdrantReadRateLimiterConfig.getCustomReadRateLimiter()).thenReturn(dummyLimiter)
        whenever(qdrantWriteRateLimiterConfig.getCustomWriteRateLimiter()).thenReturn(dummyLimiter)
    }

    @Test
    fun test_UpsertPoint_PositiveCase() {
        runBlocking {
            // Arrange
            val uploadRequest = ProductUploadRequest(
                vector = listOf(0.1f, 0.2f, 0.3f),
                productName = "test_product",
                uid = validTestUid
            )
            val updateResult = Points.UpdateResult.getDefaultInstance()
            whenever(qdrantService.upsertPoint(any())).thenReturn(updateResult)

            // Act
            val result = mockMvc.perform(
                post("/versatile_api/vector_database")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(uploadRequest))
            )

            // Assert
            result.andExpect(status().isOk())
        }
    }

    @Test
    fun test_GetAllPoints_PositiveCase() {
        runBlocking {
            // Arrange
            val scrollResponse = Points.ScrollResponse.getDefaultInstance()
            val responseDto = PointResponseDto()

            whenever(qdrantService.getAllPointsByUid(validTestUid)).thenReturn(listOf(scrollResponse))
            whenever(pointResponseDTOMapper.apply(any())).thenReturn(responseDto)

            // Act
            val result = mockMvc.perform(
                get("/versatile_api/vector_database/getAll")
                    .param("uid", validTestUid)
            )
                .andExpect(request().asyncStarted())
                .andReturn()

            // Assert
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").exists())
        }
    }

    @Test
    fun test_GetAllPoints_EmptyCase() {
        runBlocking {
            // Arrange
            whenever(qdrantService.getAllPointsByUid(validTestUid)).thenReturn(listOf())

            // Act
            val result = mockMvc.perform(
                get("/versatile_api/vector_database/getAll")
                    .param("uid", validTestUid)
            )
                .andExpect(request().asyncStarted())
                .andReturn()

            // Assert
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
        }
    }

    @Test
    fun test_GetOneOrMorePoints_PositiveCase() {
        runBlocking {
            // Arrange
            val productName = "test_product"
            val scrollResponse = Points.ScrollResponse.getDefaultInstance()
            val responseDto = PointResponseDto()

            whenever(qdrantService.getPointsByPayloads(any())).thenReturn(listOf(scrollResponse))
            whenever(pointResponseDTOMapper.apply(any())).thenReturn(responseDto)

            // Act
            val result = mockMvc.perform(
                get("/versatile_api/vector_database/getOneOrMore")
                    .param("uid", validTestUid)
                    .param("productName", productName)
            )
                .andExpect(request().asyncStarted())
                .andReturn()

            // Assert
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").exists())
        }
    }

    @Test
    fun test_GetOneOrMorePoints_EmptyCase() {
        runBlocking {
            // Arrange
            val productName = "non_existent_product"
            whenever(qdrantService.getPointsByPayloads(any())).thenReturn(listOf())

            // Act
            val result = mockMvc.perform(
                get("/versatile_api/vector_database/getOneOrMore")
                    .param("uid", validTestUid)
                    .param("productName", productName)
            )
                .andExpect(request().asyncStarted())
                .andReturn()

            // Assert
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
        }
    }

    @Test
    fun test_DeleteAllPoints_PositiveCase() {
        runBlocking {
            // Arrange
            val updateResult = Points.UpdateResult.getDefaultInstance()
            val responseDto = UpdatedPointResponseDTO()

            whenever(qdrantService.deleteAllPointsByUid(validTestUid)).thenReturn(listOf(updateResult))
            whenever(updatedPointResponseDTOMapper.apply(any())).thenReturn(responseDto)

            // Act
            val result = mockMvc.perform(
                delete("/versatile_api/vector_database")
                    .param("uid", validTestUid)
            )
                .andExpect(request().asyncStarted())
                .andReturn()

            // Assert
            mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"))
        }
    }
}