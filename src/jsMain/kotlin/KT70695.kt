package org.example

import kotlinx.benchmark.*
import kotlin.math.abs

// Code below was copied from https://youtrack.jetbrains.com/issue/KT-70695/Float-Double.isFinite-can-be-optimized#focus=Comments-27-10774274.0-0
// and slightly modified.

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
open class KotlinBenchmark {
    val data = FloatArray(Int.MAX_VALUE - 0x7f000000)

    @Setup
    fun setup() {
        for (i in data.indices) {
            data[i] = Float.fromBits(0x7f000000 + i)
        }
    }

    @Benchmark
    fun isFinite(): Boolean {
        var r = false
        for (f in data) {
            r = r or f.isFinite()
        }
        return r
    }

    @Benchmark
    fun fastIsFinite(): Boolean {
        var r = false
        for (f in data) {
            r = r or f.fastIsFinite()
        }
        return r
    }

    @Benchmark
    fun javaLangIsFiniteInlined(): Boolean {
        var r = false
        for (f in data) {
            r = r or f.javaLangIsFiniteInlined()
        }
        return r
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Float.fastIsFinite(): Boolean = (toRawBits() and 0x7fffffff) < 0x7f800000

@Suppress("NOTHING_TO_INLINE")
inline fun Float.javaLangIsFiniteInlined(): Boolean = abs(this) <= Float.MAX_VALUE
