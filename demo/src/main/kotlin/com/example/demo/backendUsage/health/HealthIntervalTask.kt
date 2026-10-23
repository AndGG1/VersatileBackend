package com.example.demo.backendUsage.health

import com.example.demo.backendUsage.log
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class HealthIntervalTask(
    private val healthService: HealthService,
    private val notifierService: NotifierService,

    @Qualifier("healthCheckTaskScheduler")
    private val taskScheduler: TaskScheduler
) {

    fun start() {
        taskScheduler.scheduleAtFixedRate(
            {
                try {
                    runAtFixedRate()
                } catch (e: Exception) {
                    log.error(e.message)
                }
            },
            Duration.ofMinutes(30)
        )
    }

    fun runAtFixedRate() {
        val qdrantStatus = healthService.checkQdrant()
        val mongoStatus = healthService.checkMongo()
        val redisStatus = healthService.checkRedis()

        val qdrantStatusString = qdrantStatus.joinToString(separator = "\n") { it.toString() }
        val reportHtml = qdrantStatusString + mongoStatus.toString() + redisStatus.toString()
        notifierService.sendAlert("Cloud Services Status Report", reportHtml)
    }
}