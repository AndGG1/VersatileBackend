package com.example.demo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

//TODO: Add build config.

@Configuration
class RestClientBuilderConfig {

    @Bean
    fun restClientBuilder(): RestClient.Builder {
        return RestClient.builder()
    }
}