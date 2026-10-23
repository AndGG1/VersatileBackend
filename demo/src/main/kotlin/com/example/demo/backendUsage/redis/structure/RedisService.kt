package com.example.demo.backendUsage.redis.structure

import com.example.demo.backendUsage.health.NotifierService
import com.example.demo.backendUsage.log
import com.example.demo.backendUsage.redis.errorHandlers.decideWhatToThrowRedis
import org.springframework.stereotype.Service

data class ParseShard(
    val shardId: String,
    val clusterUrl: String,
    val apiKey: String,
    val isAvailable: Boolean
)

@Service
class RedisService(private val redisRepository: RedisRepository,
    private val notifierService: NotifierService) {

    fun upsert(parseShard: ParseShard) {
        try {
            redisRepository.save(parseShard)
        } catch (e: Exception) {
            log.error(e.message)
            notifierService.sendAlert("Redis Service", e.message + "")

            decideWhatToThrowRedis(e)
        }
    }

    fun getShard(shardId: String): ShardData? {
        var res: ShardData? = null
        try {
            res = redisRepository.findById(shardId)
        } catch (e: Exception){
            log.error(e.message)
            notifierService.sendAlert("Redis Service", e.message + "")

            decideWhatToThrowRedis(e)
        }

        if (res == null) throw NullPointerException()
        return res
    }

    fun getCurrentActiveShard(): ShardData? {
        var res: ShardData? = null
        try {
            for (shardId in 0..2) {
                val shard = getShard("" + shardId)
                if (shard?.isAvailable == true) res = shard
            }
        } catch (e: Exception) {
            log.error(e.message)
            notifierService.sendAlert("Redis Service", e.message + "")

            decideWhatToThrowRedis(e)
        }

        if (res == null) throw NullPointerException()
        return res
    }

    fun removeShard(shardId: String) {
        try {
            redisRepository.delete(shardId)
        } catch (e: Exception) {
            log.error(e.message)
            notifierService.sendAlert("Redis Service", e.message + "")

            decideWhatToThrowRedis(e)
        }
    }
}