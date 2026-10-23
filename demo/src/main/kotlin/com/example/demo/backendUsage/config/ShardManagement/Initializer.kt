package com.example.demo.backendUsage.config.ShardManagement

import com.example.demo.backendUsage.health.HealthIntervalTask
import com.example.demo.backendUsage.redis.structure.ParseShard
import com.example.demo.backendUsage.redis.structure.RedisService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class Initializer(private val redisService: RedisService,
                  private val shardIntervalTask: ShardIntervalTask,
                  private val healthIntervalTask: HealthIntervalTask) : ApplicationRunner {

    @Value("\${qdrant.api.tokens}")
    private lateinit var apiKeys: String

    @Value("\${qdrant.cluster.urls}")
    private lateinit var clusterUrls: String

    @Value("\${shards.amount}")
    private lateinit var amount: String

    override fun run(args: ApplicationArguments?) {
        val apiKeysArr: Array<String?> = apiKeys
            .split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val clusterUrlsArr: Array<String?> =
            clusterUrls.split(", "
                .toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (cluster in 0..(amount.toInt()-1)) {
            redisService.upsert(
                ParseShard(
                    cluster.toString(),
                    clusterUrlsArr[cluster]!!,
                    apiKeysArr[cluster]!!,
                    true
                )
            )
        }

        shardIntervalTask.start()
        healthIntervalTask.start()
    }
}