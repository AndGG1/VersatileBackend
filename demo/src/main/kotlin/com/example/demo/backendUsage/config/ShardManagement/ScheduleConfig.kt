package com.example.demo.backendUsage.config.ShardManagement

import com.example.demo.backendUsage.log
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
@EnableScheduling
class SchedulerConfig {

    @Bean
    fun shardTaskScheduler(): TaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = 2
        threadPoolTaskScheduler.setThreadNamePrefix("IntervalTask-")
        threadPoolTaskScheduler.isRemoveOnCancelPolicy = true
        threadPoolTaskScheduler.setErrorHandler { task ->
            log.error(
                "Repeating Task for tracking Qdrant Shards failed. Exception interfered and operations continue running.",
                task)
        }
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true)

        threadPoolTaskScheduler.initialize()
        return threadPoolTaskScheduler
    }
}