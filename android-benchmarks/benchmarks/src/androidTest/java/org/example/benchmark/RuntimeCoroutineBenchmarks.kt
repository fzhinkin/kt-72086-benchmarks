package org.example.benchmark

import androidx.benchmark.BlackHole
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.sequences.forEach
import kotlin.time.Duration

abstract class AndroidBenchmarkBase {
    @get:Rule
    val benchmarkRule = BenchmarkRule()
}

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class ContinuationCreationBenchmark : AndroidBenchmarkBase() {
    private val emptyContinuation = Continuation<Unit>(EmptyCoroutineContext) {}

    @Test
    fun createCoroutine() {
        benchmarkRule.measureRepeated {
            BlackHole.consume(::suspendFun.createCoroutine(emptyContinuation))
        }
    }

    @Test
    fun createSuspendAndResume() {
        benchmarkRule.measureRepeated {
            ::suspendAndResume.createCoroutine(emptyContinuation).resume(Unit)
        }
    }

}

private val emptySequence = sequence<Any> {
}

private val infiniteSequence = sequence<Any> {
    while (true) {
        yield(Unit)
    }
}

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class SequencesHasNextBenchmark : AndroidBenchmarkBase() {
    @Test
    fun emptyHasNext() {
        benchmarkRule.measureRepeated {
            BlackHole.consume(emptySequence.iterator().hasNext())
        }
    }

    @Test
    fun infiniteHasNext() {
        benchmarkRule.measureRepeated {
            BlackHole.consume(infiniteSequence.iterator().hasNext())
        }
    }
}

@RunWith(Parameterized::class)
class SequencesConsumeBenchmark(private val consumedItems: Int) : AndroidBenchmarkBase() {
    @Test
    fun consumeInfinite() {
        benchmarkRule.measureRepeated {
            infiniteSequence.take(consumedItems).forEach { BlackHole.consume(it) }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "consumedItems={0}")
        fun parameters(): List<Int> = listOf(1, 10, 100, 1000)
    }
}

@RunWith(Parameterized::class)
class ContextSwitchBenchmark(private val yields: Int) : AndroidBenchmarkBase() {
    @Test
    fun contextSwitch() {
        benchmarkRule.measureRepeated {
            runBlocking {
                withContext(Dispatchers.Default) {
                    repeat(yields) {
                        BlackHole.consume(it)
                        yield()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "yields={0}")
        fun parameters(): List<Int> = listOf(1, 10, 100, 10000)
    }
}

private suspend fun suspendFun() {
}

private suspend fun suspendAndResume(): Unit {
    suspendCoroutine { continuation ->
        continuation.resume(Unit)
    }
}
