package com.example.demo.backendUsage.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient

//TODO: Add more build config. if needed.

data class WrappedHttpClient(
    val client: RestClient,
    val apiKey: String,
    val clusterUrl: String
)

@Configuration
class RestClientBuilderConfig {

    @Bean
    fun restClientBuilder(): RestClient.Builder {
        return RestClient.builder()
    }

    @Bean
    fun getHttpClientConfig(restClientBuilder: RestClient.Builder, apiKey: String, clusterUrl: String) : WrappedHttpClient {
        return WrappedHttpClient(
            restClientBuilder
                .baseUrl(clusterUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
                .build(),
            apiKey,
            clusterUrl
        )
    }
}