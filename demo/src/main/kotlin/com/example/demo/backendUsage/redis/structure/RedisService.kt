package com.example.demo.backendUsage.redis.structure

import com.example.demo.backendUsage.redis.errorHandlers.decideWhatToThrowRedis
import org.springframework.stereotype.Service

data class ParseShard(
    val shardId: String,
    val clusterUrl: String,
    val apiKey: String,
    val isAvailable: Boolean
)

@Service
class RedisService(private val redisRepository: RedisRepository) {

    fun upsert(parseShard: ParseShard) {
        try {
            redisRepository.save(parseShard)
        } catch (e: Exception) {
            decideWhatToThrowRedis(e)
        }
    }

    fun getShard(shardId: String): ShardData? {
        var res: ShardData? = null
        try {
            res = redisRepository.findById(shardId)
        } catch (e: Exception){
            decideWhatToThrowRedis(e)
        }

        if (res == null) throw NullPointerException()
        return res
    }

    fun removeShard(shardId: String) {
        try {
            redisRepository.delete(shardId)
        } catch (e: Exception) {
            decideWhatToThrowRedis(e)
        }
    }
}