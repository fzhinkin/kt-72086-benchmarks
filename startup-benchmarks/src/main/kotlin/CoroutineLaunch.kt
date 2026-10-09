package org.jetbrains.kotlin

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

suspend fun main() {
    val startTime = ProcessHandle.current().info().startInstant().get()
    val jvmLoadTime = startTime.until(Instant.now(), ChronoUnit.NANOS)

    coroutineScope {
        launch {
            val insideCoroutineTime = startTime.until(Instant.now(), ChronoUnit.NANOS)
            println("$jvmLoadTime; $insideCoroutineTime")
        }
    }
}
