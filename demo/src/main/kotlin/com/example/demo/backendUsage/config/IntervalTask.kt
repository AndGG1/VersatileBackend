package com.example.demo.backendUsage.config

import com.example.demo.backendUsage.redis.structure.ParseShard
import com.example.demo.backendUsage.redis.structure.RedisService
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class IntervalTask(
    private val redisService: RedisService,
    private val restClientBuilderConfig: RestClientBuilderConfig,
    private val activeQdrantConfig: ActiveQdrantConfig,
    private val taskScheduler: TaskScheduler
) {

    fun start() {
        taskScheduler.scheduleAtFixedRate(
            { runAtFixedRate() },
            Duration.ofSeconds(10)
        )
    }

    fun runAtFixedRate() {
        println("ran")

        for (cluster in 0 .. 2) {

            val shardId = "" + cluster
            val shardData = redisService.getShard(shardId)

            if (shardData?.isAvailable == true) {

                val availabilityCheck = restClientBuilderConfig.isClusterStillAvailable(
                    restClientBuilderConfig.restClientBuilder(),
                    shardData.apiKey,
                    shardData.clusterUrl
                )

                if (!availabilityCheck) {
                    redisService.upsert(ParseShard(
                        shardId,
                        shardData.clusterUrl,
                        shardData.apiKey,
                        false
                    ))
                } else {

                    activeQdrantConfig.upsertCurrentClient(shardData.clusterUrl, shardData.apiKey)
                }

                break
            }
        }
    }
}