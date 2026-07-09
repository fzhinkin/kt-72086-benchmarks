package org.jetbrains.kotlin

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

suspend fun main() {
    coroutineScope {
        launch {
            val startTime = ProcessHandle.current().info().startInstant().get()
            println(startTime.until(Instant.now(), ChronoUnit.NANOS))
        }
    }
}
