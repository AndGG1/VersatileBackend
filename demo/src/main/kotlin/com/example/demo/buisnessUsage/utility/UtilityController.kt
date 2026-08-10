//package com.example.demo.buisnessUsage.utility
//
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//data class VectorRequest(
//    val semRepresentation: String,
//    val limit: Int,
//    val collectionName: String
//)
//
//@RestController
//@RequestMapping("versatile_api/utility")
//class UtilityController(private val service: UtilityService) {
//
//    @GetMapping
//    fun getSimilarVectors(@RequestBody requestBody: VectorRequest): List<String> {
//        return service.findSimilarVectors(
//            requestBody.semRepresentation,
//            requestBody.limit,
//            requestBody.collectionName
//        )
//    }
//}