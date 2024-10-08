import kotlinx.benchmark.gradle.JvmBenchmarkTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.12"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain((properties["jvmVersion"]!! as String).toInt())

    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.12")
            }
        }
    }
}

benchmark {
    targets {
        register("jvm") {
            this as JvmBenchmarkTarget
            jmhVersion = "1.37"
        }
    }

    configurations {
        named("main") {
            advanced("jvmForks", 3)
        }
    }
}
