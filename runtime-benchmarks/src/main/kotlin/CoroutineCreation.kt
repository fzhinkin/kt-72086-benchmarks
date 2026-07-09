package org.jetbrains.kotlin

import kotlinx.benchmark.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import org.openjdk.jmh.annotations.Fork

@Fork(3)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
open class ContinuationCreation {
    private val emptyContinuation = Continuation<Unit>(EmptyCoroutineContext) {}

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    fun createCoroutine(): Continuation<Unit> = ::suspendFun.createCoroutine(emptyContinuation)

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    fun createSuspendAndResume() {
        ::suspendAndResume.createCoroutine(emptyContinuation).resume(Unit)
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(100)
    @Warmup(iterations = 0, time = 1)
    @Measurement(iterations = 1, time = 1)
    fun createCoroutineCold(): Continuation<Unit> = ::suspendFun.createCoroutine(emptyContinuation)

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(100)
    @Warmup(iterations = 0, time = 1)
    @Measurement(iterations = 1, time = 1)
    fun launchCoroutineCold() = runBlocking {
        launch { /* do nothing */ }
    }
}

@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
open class ContinuationCreationWarm {
    private val emptyContinuation = Continuation<Unit>(EmptyCoroutineContext) {}

    @Setup
    fun launch(): Unit = runBlocking { launch { } }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(100)
    @Warmup(iterations = 0, time = 1)
    @Measurement(iterations = 1, time = 1)
    fun createCoroutineWarm(): Continuation<Unit> = ::suspendFun.createCoroutine(emptyContinuation)

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(100)
    @Warmup(iterations = 0, time = 1)
    @Measurement(iterations = 1, time = 1)
    fun launchCoroutineWarm() = runBlocking {
        launch { /* do nothing */ }
    }
}

private suspend fun suspendFun() { /* do nothing */ }

private suspend fun suspendAndResume(): Unit {
    suspendCoroutine { continuation ->
        continuation.resume(Unit)
    }
}
