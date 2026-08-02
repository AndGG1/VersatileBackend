package com.example.demo.backendUsage.redis.errorHandlers

import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.net.SocketTimeoutException
import java.util.concurrent.ExecutionException

fun decideWhatToThrowRedis(exception: Exception) {
    val cause = if (exception is ExecutionException) exception.cause ?: exception else exception

    when (cause) {
        is RedisConnectionFailureException, is SocketTimeoutException ->
            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Redis cache or database is down or unreachable", cause)

        is ClassCastException ->
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Redis data deserialization mismatch or type casting error", cause)

        is IllegalArgumentException ->
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid key or argument provided to Redis operation", cause)

        is NullPointerException ->
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal null reference in Redis layer", cause)

        else ->
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Redis error: ${cause.message}", cause)
    }
}