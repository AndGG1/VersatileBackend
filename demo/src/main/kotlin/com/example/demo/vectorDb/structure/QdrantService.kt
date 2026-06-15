package com.example.demo.vectorDb.structure

import org.springframework.stereotype.Service

@Service
class QdrantService(private val repository: QdrantRepository) {

    fun upsertPoint(pointRequest: QdrantPointRequest) {
        repository.save(pointRequest)
    }
}