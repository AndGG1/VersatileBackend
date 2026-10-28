package com.example.demo.backendUsage.config.ShardManagement

import com.example.demo.backendUsage.config.RestClientBuilderConfig
import com.example.demo.backendUsage.health.NotifierService
import com.example.demo.backendUsage.log
import com.example.demo.backendUsage.redis.structure.ParseShard
import com.example.demo.backendUsage.redis.structure.RedisService
import com.example.demo.buisnessUsage.vectorDb.structure.isClusterStillAvailable
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ShardIntervalTask(
    private val redisService: RedisService,
    private val restClientBuilderConfig: RestClientBuilderConfig,
    private val activeQdrantConfig: ActiveQdrantConfig,

    @Qualifier("shardTaskScheduler")
    private val taskScheduler: TaskScheduler,

    private val notifierService: NotifierService
) {

    fun start() {
        taskScheduler.scheduleAtFixedRate(
            {
                try {
                    runAtFixedRate()
                } catch (e: Exception) {
                    log.error(e.message)
                    notifierService.sendAlert("Shard Management Error", e.message + "")
                }
            },
            Duration.ofSeconds(5)
        )
    }

    @Value("\${shards.amount}")
    private lateinit var amount: String
    fun runAtFixedRate() {
        log.info("INTERNAL BACKEND OPERATION: Shard Management has performed a check " +
                "to decide whether requests will be redirected towards a new Shard.")

        for (cluster in 0 .. (amount.toInt()-1)) {
            val shardId = "" + cluster
            val shardData = redisService.getShard(shardId)

            if (shardData?.isAvailable == true) {

                val availabilityCheck = isClusterStillAvailable(
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
