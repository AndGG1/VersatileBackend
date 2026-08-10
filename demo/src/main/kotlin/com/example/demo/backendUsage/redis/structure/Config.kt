package com.example.demo.backendUsage.redis.structure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import redis.clients.jedis.JedisPoolConfig
import java.net.URI
import java.time.Duration

@Configuration
class RedisConfig {
    @Value("\${redis.endpoint}")
    lateinit var redisEndpoint: String

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val uri = URI.create(redisEndpoint)

        val standaloneConfig = RedisStandaloneConfiguration().apply {
            hostName = uri.host
            port = if (uri.port > 0) uri.port else 6379

            uri.userInfo?.let { userInfo ->
                if (userInfo.contains(":")) {
                    val parts = userInfo.split(":", limit = 2)
                    username = parts[0]
                    setPassword(RedisPassword.of(parts[1]))
                } else {
                    setPassword(RedisPassword.of(userInfo))
                }
            }
        }

        val poolConfig = JedisPoolConfig().apply {
            maxTotal = 1
            maxIdle = 1
            minIdle = 1
            testOnBorrow = true
        }

        val clientConfigBuilder = JedisClientConfiguration.builder()
            .usePooling().poolConfig(poolConfig).and()
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(5))

        if (uri.scheme == "rediss") {
            clientConfigBuilder.useSsl()
        }

        return JedisConnectionFactory(standaloneConfig, clientConfigBuilder.build())
    }

    @Bean
    fun redisTemplate(connectionFactory: JedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory

        val stringSerializer = StringRedisSerializer()
        template.keySerializer = stringSerializer
        template.hashKeySerializer = stringSerializer

        val jsonSerializer = GenericJackson2JsonRedisSerializer()
        template.valueSerializer = jsonSerializer
        template.hashValueSerializer = jsonSerializer

        template.afterPropertiesSet()
        return template
    }
}
