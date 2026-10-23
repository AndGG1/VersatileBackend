package com.example.demo.backendUsage.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.net.http.HttpClient
import java.time.Duration

data class WrappedHttpClient(
    val client: RestClient,
    val apiKey: String,
    val clusterUrl: String
)

@Configuration
class RestClientBuilderConfig {

    @Bean
    fun restClientBuilder(): RestClient.Builder {
        val httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build()

        val requestFactory = JdkClientHttpRequestFactory(httpClient).apply {
            setReadTimeout(Duration.ofSeconds(10))
        }

        return RestClient.builder()
            .requestFactory(requestFactory)
    }
}