package com.example.demo.backendUsage.config.RateLimiting.UserRateLimiting

import com.google.common.util.concurrent.RateLimiter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserReadRateLimiterConfig {

    @Value("\${user.read.rate.limit}")
    private lateinit var rateLimit: String

    @Bean
    fun getUserCustomReadRateLimiter(): RateLimiter {
        return RateLimiter.create(rateLimit.toDouble())
    }
}