package com.example.demo.backendUsage.redis.structure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Value("\${redis.endpoint}")
lateinit var REDIS_ENDPOINT: String

@Bean
fun jedisConnectionFactory() : JedisConnectionFactory {
    val config = RedisStandaloneConfiguration(REDIS_ENDPOINT, 6379)
    return JedisConnectionFactory(config)
}

@Bean
fun redisTemplate() : RedisTemplate<String, Any> {
    val template: RedisTemplate<String, Any> = RedisTemplate()
    template.connectionFactory = jedisConnectionFactory()

    return template
}