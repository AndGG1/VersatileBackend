package com.example.demo.backendUsage.health

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

//Informative Comment: Simple liveness check fot the backend's health.
//Informative Comment: pinged every 5 minutes, such that the Render Server doesn't suspend.

@RestController
@RequestMapping("/versatile_api/health")
class HealthController {

    @GetMapping
    fun ping() {
        //this returns nothing
    }
}