package com.example.demo.backendUsage.config.RateLimiting.QdrantWriteRateLimiterConfig

import com.google.common.util.concurrent.RateLimiter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QdrantWriteRateLimiterConfig {

    @Value("\${qdrant.write.rate.limit}")
    private lateinit var rateLimit: String

    @Bean
    fun getCustomWriteRateLimiter(): RateLimiter {
        return RateLimiter.create(rateLimit.toDouble())
    }
}