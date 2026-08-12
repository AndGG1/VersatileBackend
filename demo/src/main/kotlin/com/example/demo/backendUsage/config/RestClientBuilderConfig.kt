package com.example.demo.backendUsage.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.net.http.HttpClient
import java.time.Duration
import java.util.regex.Pattern

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
        val httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build()

        val requestFactory = JdkClientHttpRequestFactory(httpClient).apply {
            setReadTimeout(Duration.ofSeconds(10))
        }

        return RestClient.builder()
            .requestFactory(requestFactory)
    }

    fun isClusterStillAvailable(restClientBuilder: RestClient.Builder, apiKey: String, clusterUrl: String): Boolean {
        val client = WrappedHttpClient(
            restClientBuilder
                .baseUrl(clusterUrl)
                .defaultHeader(com.google.common.net.HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
                .build(),
            apiKey,
            clusterUrl
        )

        val softBlockDisk = 3_200_000_000; val softBlockRam = 750_000_000
        val response: String? = client
            .client
            .get()
            .uri("/collections/VersatilePTT_VectorCollection/memory")
            .retrieve()
            .body(String::class.java)

        if (response != null && !response.isBlank()) {
            var matcher =
                Pattern.compile("\"disk_bytes\":(?<memoryUsedInBytes>\\d+)").matcher(response)
            matcher.find()
            val memoryUsedDisk: String = matcher.group("memoryUsedInBytes")
            var convertedMemoryUsedToBytes: Long = memoryUsedDisk.toLong()

            if (convertedMemoryUsedToBytes > softBlockDisk) return false


            matcher = Pattern.compile("\"ram\":(?<memoryUsedInBytes>\\d+)").matcher(response)
            matcher.find()
            val memoryUsedRam: String = matcher.group("memoryUsedInBytes")
            convertedMemoryUsedToBytes = memoryUsedRam.toLong()

            if (convertedMemoryUsedToBytes > softBlockRam) return false
        }

        return true
    }
}