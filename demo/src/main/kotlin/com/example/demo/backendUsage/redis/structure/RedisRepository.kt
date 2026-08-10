package com.example.demo.backendUsage.redis.structure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ShardData(
    @JsonProperty("clusterUrl") val clusterUrl: String,

    @JsonProperty("apiKey") val apiKey: String,

    @get:JsonProperty("isAvailable")
    @param:JsonProperty("isAvailable")
    val isAvailable: Boolean = true
) : Serializable

@Repository
class RedisRepository(private val redisTemplate: RedisTemplate<String, Any>) {

    //Informative Comment: In Redis, a hash key refers to the name of the COLUMN of a specific ROW in TABLE shardId.
    private val HASH_KEY: String = "column_1"

    fun save(parseShard: ParseShard) {
        println("saved")
        val key = "$HASH_KEY:${parseShard.shardId}"
        val value = ShardData(
            parseShard.clusterUrl,
            parseShard.apiKey,
            parseShard.isAvailable
        )

        redisTemplate.opsForHash<String, ShardData>().put(key, HASH_KEY, value)
    }

    fun findById(shardId: String): ShardData? {
        println("looked for $shardId")
        val key = "$HASH_KEY:$shardId"

        return redisTemplate.opsForHash<String, ShardData>().get(key, HASH_KEY)
    }

    fun delete(shardId: String) {
        val key = "$HASH_KEY:$shardId"
        redisTemplate.delete(key)
    }
}