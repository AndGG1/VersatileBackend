package com.example.demo.backendUsage.redis.structure

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

data class ShardData(
    val clusterUrl: String,
    val apiKey: String,
    val isAvailable: Boolean
)

@Repository
class RedisRepository(private val redisTemplate: RedisTemplate<String, Any>) {

    //Informative Comment: In Redis, a hash key refers to the name of the COLUMN of a specific ROW in TABLE shardId.
    private val HASH_KEY: String = "column_1"

    fun save(parseShard: ParseShard) {
        val key = "$HASH_KEY:${parseShard.shardId}"
        val value = ShardData(
            parseShard.clusterUrl,
            parseShard.apiKey,
            parseShard.isAvailable
        )

        redisTemplate.opsForHash<String, ShardData>().put(key, HASH_KEY, value)
    }

    fun findById(shardId: String): ShardData? {
        val key = "$HASH_KEY:$shardId"
        val entries = redisTemplate.opsForHash<String, ShardData>().entries(key)

        if (entries.isEmpty()) return null

        return ShardData(
            clusterUrl = (entries["clusterUrl"] ?: "") as String,
            apiKey = (entries["apiKey"] ?: "") as String,
            isAvailable = (entries["isAvailable"] ?: true) as Boolean
        )
    }

    fun delete(shardId: String) {
        val key = "$HASH_KEY:$shardId"
        redisTemplate.delete(key)
    }
}