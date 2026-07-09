package org.jetbrains.kotlin

import kotlinx.benchmark.*
import org.openjdk.jmh.annotations.Fork
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(3)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
open class Sequences {
    val emptySequence = sequence<Any> {
    }

    val infiniteSequence = sequence<Any> {
        while (true) {
            yield(Unit)
        }
    }

    @Benchmark
    fun emptyHasNext() = emptySequence.iterator().hasNext()

    @Benchmark
    fun infiniteHasNext() = infiniteSequence.iterator().hasNext()

    @Benchmark
    fun consumeInfinite(blackhole: Blackhole) {
        infiniteSequence.take(10).forEach { blackhole.consume(it) }
    }
}
