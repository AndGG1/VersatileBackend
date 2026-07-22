package com.example.demo.embedding.structure

import com.example.demo.embedding.structure.errorHandlers.ServiceResponseErrorHandlerKt
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class EmbeddingService(restClientBuilder: RestClient.Builder, serviceResponseErrorHandler: ServiceResponseErrorHandlerKt) {
    private val client = restClientBuilder.build()

    private val errorHandler = serviceResponseErrorHandler
    @Value("\${hf.api.token}")
    private lateinit var HF_TOKEN: String

    private val HF_URL = "https://router.huggingface.co/hf-inference/models/dumitrescustefan/bert-base-romanian-cased-v1/pipeline/feature-extraction"

    fun embed(inputs: List<String>): EmbedResponse {
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

        return EmbedResponse(processedEmbeddings)
    }
}