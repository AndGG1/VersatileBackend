package com.example.demo.embedding.structure

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class EmbeddingService {

    private val client = RestClient.create()
    private val HF_TOKEN = "token"
    private val HF_URL = "https://router.huggingface.co/hf-inference/models/sentence-transformers/all-MiniLM-L6-v2/pipeline/feature-extraction"

    fun embed(inputs: List<String>): EmbedResponse {
        return EmbedResponse(
            client.post()
            .uri(HF_URL)
            .header("Authorization", "Bearer $HF_TOKEN")
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("inputs" to inputs))
            .retrieve()
            .body(object : ParameterizedTypeReference<List<List<Double>>>() {})
            ?: emptyList()
        )
    }
}