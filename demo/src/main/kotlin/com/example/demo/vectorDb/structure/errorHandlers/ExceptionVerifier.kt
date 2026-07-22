package com.example.demo.vectorDb.structure.errorHandlers

import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.ExecutionException

fun decideWhatToThrow(exception: Exception) {
    val cause = if (exception is ExecutionException) exception.cause ?: exception else exception

    when (cause) {
        is StatusRuntimeException -> {
            when (cause.status.code) {
                Status.Code.ALREADY_EXISTS ->
                    throw ResponseStatusException(HttpStatus.CONFLICT, "Point or vector already exists", cause)

                Status.Code.UNAVAILABLE, Status.Code.DEADLINE_EXCEEDED ->
                    throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Qdrant vector database is down or unreachable", cause)

                Status.Code.INVALID_ARGUMENT, Status.Code.FAILED_PRECONDITION ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid vector shape, payload format, or collection configuration", cause)

                else ->
                    throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Qdrant internal error: ${cause.status.description}", cause)
            }
        }

        is IllegalArgumentException ->
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input data provided to the service", cause)

        is NullPointerException ->
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal server error. Advised to try again!", cause)

        else -> throw cause
    }
}