package com.example.demo.buisnessUsage.vectorDb.errorHandlers

import com.example.demo.buisnessUsage.vectorDb.structure.QdrantController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

data class ErrorBody(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?
)

@RestControllerAdvice(assignableTypes = [QdrantController::class])
class QdrantControllerAdvice {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleExpectedException(e: ResponseStatusException): ResponseEntity<ErrorBody> {
        val errBody = ErrorBody(
            status = e.statusCode.value(),
            error = HttpStatus.valueOf(e.statusCode.value()).reasonPhrase,
            message = e.reason
        )
        return ResponseEntity(errBody, e.statusCode)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(e: Exception): ResponseEntity<ErrorBody> {
        val errBody = ErrorBody(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Exception",
            message = e.message
        )
        return ResponseEntity(errBody, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}