package org.jetbrains.kotlin

import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.Level
import kotlinx.benchmark.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openjdk.jmh.annotations.Fork
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(3)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
open class CancellationBenchmark {
    @Param("1", "10", "100")
    public var coroutines: Int = 10

    @Param("Default", "IO", "Unconfined")
    public var dispatcherType: String = "Default"

    private var context: CoroutineScope? = null
    private var dispatcher = Dispatchers.Default

    @Setup
    fun setup() {
        dispatcher = when (dispatcherType) {
            "Default" -> Dispatchers.Default
            "IO" -> Dispatchers.IO
            "Unconfined" -> Dispatchers.Unconfined
            else -> throw IllegalArgumentException("Invalid dispatcher type: $dispatcherType")
        }
    }

    @Setup(Level.Invocation)
    fun launchAll() {
        context = CoroutineScope(dispatcher)
        repeat(coroutines) {
            with(context!!) {
                launch {
                    waitIndefinitely()
                }
            }
        }
    }

    @Benchmark
    fun cancel() {
        context!!.cancel()
    }
}

private suspend fun waitIndefinitely() {
    delay(Duration.INFINITE)
}
