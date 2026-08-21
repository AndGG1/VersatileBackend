package com.example.demo.backendUsage.config

import com.google.common.util.concurrent.RateLimiter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserRateLimiterConfig {

    @Value("\${user.rate.limit}")
    private lateinit var rateLimit: String

    @Bean
    fun getUserCustomRateLimiter(): RateLimiter {
        return RateLimiter.create(rateLimit.toDouble())
    }
}