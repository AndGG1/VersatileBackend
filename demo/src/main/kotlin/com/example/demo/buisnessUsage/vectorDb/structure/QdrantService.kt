package com.example.demo.buisnessUsage.vectorDb.structure

import com.example.demo.buisnessUsage.vectorDb.errorHandlers.decideWhatToThrowQdrant
import io.qdrant.client.grpc.Points
import org.springframework.stereotype.Service

@Service
class QdrantService(private val repository: QdrantRepository) {

    fun upsertPoint(pointRequest: QdrantPointRequest) {
        try {
            val uid: String = pointRequest.payload.uid
            if (uid.isBlank() || !uid.matches(Regex("^[A-Za-z0-9+/_-]{28}$"))) {
                throw IllegalArgumentException()
            }
            repository.save(pointRequest)
        } catch (e: Exception) {
            decideWhatToThrowQdrant(e)
        }
    }

    suspend fun getAllPointsByUid(uid: String): List<Points.ScrollResponse> {
        val res = try {
            repository.getAll(uid).filterNotNull()
        } catch (e: Exception) {
            decideWhatToThrowQdrant(e)
            emptyList()
        }

        if (res.isEmpty()) throw NullPointerException("No responses returned from Qdrant shards")
        return res
    }

    suspend fun getPointsByPayloads(payloads: CollectionPayload): List<Points.ScrollResponse> {
        val res = try {
            repository.getOneOrMore(payloads).filterNotNull()
        } catch (e: Exception) {
            decideWhatToThrowQdrant(e)
            emptyList()
        }

        if (res.isEmpty()) throw NullPointerException("No responses returned from Qdrant shards")
        return res
    }

    suspend fun deleteAllPointsByUid(uid: String): List<Points.UpdateResult> {
        val res = try {
            repository.deleteAll(uid).filterNotNull()
        } catch (e: Exception) {
            decideWhatToThrowQdrant(e)
            emptyList()
        }

        if (res.isEmpty()) throw NullPointerException("No responses returned from Qdrant shards")
        return res
    }
}