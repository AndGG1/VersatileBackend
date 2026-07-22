package com.example.demo.embedding.structure.errorHandlers

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.ResponseErrorHandler
import java.io.IOException
import java.net.URI

@Component
class ServiceResponseErrorHandlerKt : ResponseErrorHandler {

    @Throws(IOException::class)
    override fun hasError(httpResponse: ClientHttpResponse): Boolean {
        return httpResponse.statusCode.isError
    }

    @Throws(IOException::class)
    override fun handleError(url: URI, method: HttpMethod, response: ClientHttpResponse) {
        val statusCode = response.statusCode

        if (statusCode.is5xxServerError) {
            System.err.println("HttpStatus: INTERNAL_SERVER_ERROR")
        } else if (statusCode.is4xxClientError) {
            when (statusCode) {
                HttpStatus.NOT_FOUND -> System.err.println("HttpStatus: NOT_FOUND")
                HttpStatus.BAD_REQUEST -> System.err.println("HttpStatus: BAD_REQUEST")
                else -> println("HttpStatus: UNKNOWN_ERROR")
            }
        }
    }
}