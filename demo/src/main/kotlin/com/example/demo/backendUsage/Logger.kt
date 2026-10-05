package com.example.demo.backendUsage

import org.slf4j.Logger
import org.slf4j.LoggerFactory

// Reusable logger for ANY class in your project
val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)