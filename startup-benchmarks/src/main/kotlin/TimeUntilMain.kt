package org.jetbrains.kotlin

import java.time.Instant
import java.time.temporal.ChronoUnit

fun main() {
    val startTime = ProcessHandle.current().info().startInstant().get()
    println(startTime.until(Instant.now(), ChronoUnit.NANOS))
}
