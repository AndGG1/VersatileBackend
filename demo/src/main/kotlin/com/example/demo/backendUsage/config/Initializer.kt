package com.example.demo.backendUsage.config

import com.example.demo.backendUsage.redis.structure.ParseShard
import com.example.demo.backendUsage.redis.structure.RedisService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class Initializer(private val redisService: RedisService,
                  private val intervalTask: IntervalTask) : ApplicationRunner {
    @Value("\${qdrant.api.tokens}")
    private val apiKeys: String? = null

    @Value("\${qdrant.cluster.urls}")
    private val clusterUrls: String? = null

    override fun run(args: ApplicationArguments?) {
        val apiKeysArr: Array<String?> = apiKeys!!
            .split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val clusterUrlsArr: Array<String?> =
            clusterUrls!!.split(", "
                .toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (cluster in 0..2) {
            redisService.upsert(
                ParseShard(
                    cluster.toString(),
                    clusterUrlsArr[cluster]!!,
                    apiKeysArr[cluster]!!,
                    true
                )
            )
        }

        intervalTask.start()
    }
}