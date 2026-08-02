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

    suspend fun getAllPointsByUid(uid: String): Points.ScrollResponse? {
        var res: Points.ScrollResponse? = null
        try {
            res = repository.getAll(uid)
        } catch (e: Exception) {
            decideWhatToThrowQdrant(e)
        }

        if (res == null) throw NullPointerException()
        return res
    }

    suspend fun getPointsByPayloads(payloads: CollectionPayload) : Points.ScrollResponse? {
        var res: Points.ScrollResponse? = null
        try {
            res = repository.getOneOrMore(payloads)
        } catch (e: Exception) {
            decideWhatToThrowQdrant(e)
        }

        if (res == null) throw NullPointerException()
        return res
    }

    suspend fun deleteAllPointsByUid(uid: String) : Points.UpdateResult? {
        var res: Points.UpdateResult? = null
        try {
            res = repository.deleteAll(uid)
        } catch (e: Exception) {
            decideWhatToThrowQdrant(e)
        }

        if (res == null) throw NullPointerException()
        return res
    }
}