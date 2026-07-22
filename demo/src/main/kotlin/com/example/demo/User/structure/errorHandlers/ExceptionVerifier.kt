package com.example.demo.User.structure.errorHandlers

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun decideWhatToThrow(exception: Exception) {

    when (exception) {
        is org.springframework.dao.DuplicateKeyException ->
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists") as Throwable

        is org.springframework.dao.DataAccessResourceFailureException ->
            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Database down", exception)

        is IllegalArgumentException ->
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input data")

        is NullPointerException ->
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal server error. Advised to try again!")

        else -> throw exception
    }
}
