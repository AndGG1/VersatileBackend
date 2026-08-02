//package com.example.demo.backendUsage.config
//
//import com.example.demo.buisnessUsage.vectorDb.structure.QdrantRepository
//import org.springframework.scheduling.annotation.Scheduled
//import org.springframework.stereotype.Component
//
//@Component
//class IntervalTask(private val qdrantRepository: QdrantRepository) {
//
//    @Scheduled(fixedRate = 5000)
//    fun runAtFixedRate() {
//        qdrantRepository.isClusterStillAvailable()
//    }
//}