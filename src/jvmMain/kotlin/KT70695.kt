package org.example

import kotlinx.benchmark.*
import org.openjdk.jmh.annotations.Fork
import java.util.concurrent.TimeUnit
import kotlin.math.abs

// Code below was copied from https://youtrack.jetbrains.com/issue/KT-70695/Float-Double.isFinite-can-be-optimized#focus=Comments-27-10774274.0-0
// and slightly modified.

@State(Scope.Benchmark)
open class FloatState {
    val data = FloatArray(Integer.MAX_VALUE - 0x7f000000)

    @Setup
    fun setup() {
        for (i in data.indices) {
            data[i] = Float.fromBits(0x7f000000 + i)
        }
    }
}

@Fork(value = 3)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class KotlinBenchmark {
    @Benchmark
    fun isFinite(state: FloatState): Boolean {
        var r = false
        for (f in state.data) {
            r = r or f.isFinite()
        }
        return r
    }

    @Benchmark
    fun fastIsFinite(state: FloatState): Boolean {
        var r = false
        for (f in state.data) {
            r = r or f.fastIsFinite()
        }
        return r
    }

    @Benchmark
    fun javaLangIsFinite(state: FloatState): Boolean {
        var r = false
        for (f in state.data) {
            r = r or f.javaLangIsFinite()
        }
        return r
    }

    @Benchmark
    fun javaLangIsFiniteInlined(state: FloatState): Boolean {
        var r = false
        for (f in state.data) {
            r = r or f.javaLangIsFiniteInlined()
        }
        return r
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Float.fastIsFinite(): Boolean = (toRawBits() and 0x7fffffff) < 0x7f800000

@Suppress("NOTHING_TO_INLINE")
inline fun Float.javaLangIsFinite(): Boolean = java.lang.Float.isFinite(this)

@Suppress("NOTHING_TO_INLINE")
inline fun Float.javaLangIsFiniteInlined(): Boolean = abs(this) <= Float.MAX_VALUE
