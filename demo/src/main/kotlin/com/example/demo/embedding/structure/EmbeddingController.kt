package com.example.demo.embedding.structure

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class EmbedRequest(
    val inputs: List<String>
)

data class EmbedResponse(
    val embeddings: List<List<Double>>
)

@RestController
@RequestMapping("/embeddings")
class EmbeddingController(private val embeddingService: EmbeddingService) {

    @PostMapping
    fun convertToVectors(@RequestBody request: EmbedRequest) : EmbedResponse {
        return embeddingService.embed(request.inputs)
    }
}