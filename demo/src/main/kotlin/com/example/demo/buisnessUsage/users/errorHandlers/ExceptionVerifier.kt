package com.example.demo.buisnessUsage.users.errorHandlers

import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun decideWhatToThrowUser(exception: Exception) {

    when (exception) {
        is DuplicateKeyException ->
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists") as Throwable

        is DataAccessResourceFailureException ->
            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Database down", exception)

        is IllegalArgumentException ->
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input data")

        is NullPointerException ->
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal server error. Advised to try again!")

        else -> throw exception
    }
}
