package org.jetbrains.kotlin

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Fork
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(3)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
open class LaunchAndWaitBenchmark {
    @Param("1", "8", "100")
    public var items: Int = 1

    @Param("Default", "IO", "Unconfined")
    public var dispatcherType: String = "Default"

    private var data: List<Int> = emptyList()
    private val channel = Channel<Int>(capacity = Channel.UNLIMITED)
    private var dispatcher = Dispatchers.Default

    @Setup
    fun setup() {
        data = (0..items).toList()
        dispatcher = when (dispatcherType) {
            "Default" -> Dispatchers.Default
            "IO" -> Dispatchers.IO
            "Unconfined" -> Dispatchers.Unconfined
            else -> throw IllegalArgumentException("Invalid dispatcher type: $dispatcherType")
        }
    }

    @Benchmark
    fun processItems(bh: Blackhole) = runBlocking(context = dispatcher) {
        coroutineScope {
            data.forEach { item ->
                launch {
                    channel.send(item * item)
                }
            }
        }
        while (true) {
            val result = channel.tryReceive()
            if (!result.isSuccess) break
            bh.consume(result.getOrNull())
        }
    }
}
