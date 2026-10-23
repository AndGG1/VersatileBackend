package com.example.demo.backendUsage.config

import com.example.demo.backendUsage.health.NotifierService
import com.example.demo.backendUsage.log
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
@EnableScheduling
class HealthCheckScheduleConfig(
    private val notifierService: NotifierService) {

    @Bean
    fun healthCheckTaskScheduler(): TaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = 2
        threadPoolTaskScheduler.setThreadNamePrefix("IntervalTask-")
        threadPoolTaskScheduler.isRemoveOnCancelPolicy = true
        threadPoolTaskScheduler.setErrorHandler { task ->
            log.error(
                "Health Check failed! Exception interfered, alert sent to owner(AndGG1 on Github) and process continues.",
                task)

            notifierService.sendAlert("Health Check Status",
                """
                    Health Check has failed!

                    Possible cause can be found in this error: ${task.printStackTrace()}
                """.trimIndent())
        }
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true)

        threadPoolTaskScheduler.initialize()
        return threadPoolTaskScheduler
    }
}