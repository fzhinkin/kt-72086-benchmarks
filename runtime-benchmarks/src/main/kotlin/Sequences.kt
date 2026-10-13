package org.jetbrains.kotlin

import kotlinx.benchmark.*
import org.openjdk.jmh.annotations.Fork

val emptySequence = sequence<Any> {
}

val infiniteSequence = sequence<Any> {
    while (true) {
        yield(Unit)
    }
}

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Fork(3)
@Warmup(iterations = 5, time = 3, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
open class SequencesHasNextBenchmark {
    @Benchmark
    fun emptyHasNext() = emptySequence.iterator().hasNext()

    @Benchmark
    fun infiniteHasNext() = infiniteSequence.iterator().hasNext()
}

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Fork(3)
@Warmup(iterations = 5, time = 3, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
open class SequencesConsumeBenchmark {
    @Param("1", "10", "100", "1000")
    var consumeItems: Int = 0

    @Benchmark
    fun consumeInfinite(blackhole: Blackhole) {
        infiniteSequence.take(consumeItems).forEach { blackhole.consume(it) }
    }
}
