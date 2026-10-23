package com.example.demo.backendUsage.health

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class NotifierService(
    private val restClient: RestClient = RestClient.create()
) {
    @Value("\${notifier.email}")
    private lateinit var email: String

    @Value("\${notifier.api.key}")
    private lateinit var apiKey: String

    fun sendAlert(subject: String, body: String) {

        val payload = mapOf(
            "from" to "Alerts <onboarding@resend.dev>",
            "to" to listOf(email),
            "subject" to subject,
            "html" to body
        )

        restClient.post()
            .uri("https://api.resend.com/emails")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .body(payload)
            .retrieve()
            .toBodilessEntity()
    }
}