package com.example.demo.buisnessUsage.vectorDb.structure

import com.google.common.util.concurrent.RateLimiter
import io.qdrant.client.grpc.Points
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class ProductUploadRequest(
    val vector: List<Float>,
    val productName: String,
    val uid: String
)

@RestController
@RequestMapping("/versatile_api/vector_database")
class QdrantController(private val service: QdrantService, private val getCustomRateLimiter: RateLimiter) {

    @PostMapping
    suspend fun upsertPoint(@RequestBody requestBody: ProductUploadRequest) {
        getCustomRateLimiter.acquire()

        val payload = CollectionPayload(requestBody.uid, requestBody.productName)
        val qdrantRequest = QdrantPointRequest(
            vector = requestBody.vector,
            payload = payload
        )

        service.upsertPoint(qdrantRequest)
    }

    @GetMapping("/getAll")
    suspend fun getAllPoints(@RequestParam uid: String): List<Points.ScrollResponse?> {
        getCustomRateLimiter.acquire()

       return service.getAllPointsByUid(uid)
    }

    @GetMapping("/getOneOrMore")
    suspend fun getOneOrMorePoints(@RequestParam uid: String,
                                   @RequestParam productName: String): List<Points.ScrollResponse?> {
        getCustomRateLimiter.acquire()

        return service.getPointsByPayloads(
            CollectionPayload(
                uid,
                productName)
        )
    }

    @DeleteMapping
    suspend fun deleteAllPoints(@RequestParam uid: String) : List<Points.UpdateResult?> {
        getCustomRateLimiter.acquire()

        return service.deleteAllPointsByUid(uid)
    }
}