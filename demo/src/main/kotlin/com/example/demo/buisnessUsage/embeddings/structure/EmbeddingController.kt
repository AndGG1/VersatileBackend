package com.example.demo.buisnessUsage.embeddings.structure

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
@RequestMapping("/versatile_api/embeddings")
class EmbeddingController(private val embeddingService: EmbeddingService) {

    @PostMapping
    fun convertToVectors(@RequestBody request: EmbedRequest) : EmbedResponse {
        require(request.inputs.isNotEmpty()) {"API requires non-empty list of sentences!"}

        val filteredInputs = request.inputs.filter { s -> s.isNotBlank() }
        return embeddingService.embed(filteredInputs)
    }
}