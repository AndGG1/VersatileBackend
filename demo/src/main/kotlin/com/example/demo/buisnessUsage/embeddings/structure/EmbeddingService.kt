package com.example.demo.buisnessUsage.embeddings.structure

import com.example.demo.backendUsage.health.NotifierService
import com.example.demo.backendUsage.log
import com.example.demo.buisnessUsage.embeddings.errorHandlers.ServiceResponseErrorHandlerKt
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

data class EmbedResponse(
    val embeddings: List<List<Double>>
)

@Service
class EmbeddingService(restClientBuilder: RestClient.Builder,
                       serviceResponseErrorHandler: ServiceResponseErrorHandlerKt,
                       private val notifierService: NotifierService) {
    private val client = restClientBuilder.build()

    private val errorHandler = serviceResponseErrorHandler
    @Value("\${hf.api.token}")
    private lateinit var HF_TOKEN: String

    @Value("\${hf.url}")
    private lateinit var HF_URL: String

    fun embed(inputs: List<String>): EmbedResponse {
        return try {
            val requestBody = mapOf(
                "inputs" to inputs,
                "parameters" to mapOf("wait_for_model" to true)
            )

            val response = client.post()
                .uri(HF_URL)
                .header("Authorization", "Bearer $HF_TOKEN")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .onStatus(errorHandler)
                .body(object : ParameterizedTypeReference<List<List<List<Double>>>>() {})

            val processedEmbeddings = response?.map { textTokens ->
                textTokens.firstOrNull() ?: emptyList()
            } ?: emptyList()

            EmbedResponse(processedEmbeddings)
        } catch (e: Exception) {
            log.error(e.message)
            notifierService.sendAlert("Embedding Service Error", e.message + "")

            EmbedResponse(emptyList())
        }
    }
}