package com.example.demo.backendUsage.config.Security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/versatile_api/health").permitAll()
                auth.anyRequest().authenticated() // <-- This ensures other endpoints require auth
            }
            .httpBasic(withDefaults())

        return http.build()
    }
}