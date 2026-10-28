package com.example.demo.backendUsage.config.Security

import com.example.demo.backendUsage.log
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        log.info("BACKEND SECURITY CONCERN: the health api does not require an API Key in order to be accessed.")

        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/versatile_api/health").permitAll()
                auth.anyRequest().authenticated()
            }
            .httpBasic(withDefaults())

        return http.build()
    }
}