import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    id("com.gradleup.shadow") version "9.4.2"

}

kotlin {
    jvmToolchain(21)

    val main = target.compilations["main"]
    main.defaultSourceSet.kotlin.srcDir("src/main/kotlin")
    val serviceLoader = target.compilations.create("serviceLoader")
    serviceLoader.defaultSourceSet.kotlin.srcDir("src/main/kotlin")

    configurations.create("ij")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://www.jetbrains.com/intellij-repository/releases/")
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
}

dependencies {

    api("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0")
    "serviceLoaderImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0-dpspi")
    "serviceLoaderImplementation"("org.jetbrains.kotlin:kotlin-stdlib:2.4.255-SNAPSHOT")
    "ij"("com.jetbrains.intellij.platform:ide-impl:261.24374.191")
}

tasks {
    register("mainSlimJar", ShadowJar::class) {
        isZip64 = true
        archiveBaseName.set("startup-benchmarks-main-slim")
        from(sourceSets.main.get().output)
        configurations = project.configurations.named("runtimeClasspath").map { listOf(it) }
        manifest {
            attributes("Main-Class" to "org.jetbrains.kotlin.StartupKt")
        }
    }

    register("mainIjJar", ShadowJar::class) {
        isZip64 = true
        archiveBaseName.set("startup-benchmarks-main-ij")
        from(sourceSets.main.get().output)
        configurations = project.configurations.named("runtimeClasspath").map { listOf(it) }
        configurations.addAll(project.configurations.named("ij").map { listOf(it) })
        manifest {
            attributes("Main-Class" to "org.jetbrains.kotlin.StartupKt")
        }
    }

    register("serviceLoaderSlimJar", ShadowJar::class) {
        isZip64 = true
        archiveBaseName.set("startup-benchmarks-serviceLoader-slim")
        from(sourceSets.named("serviceLoader").get().output)
        configurations = project.configurations.named("serviceLoaderRuntimeClasspath").map { listOf(it) }
        manifest {
            attributes("Main-Class" to "org.jetbrains.kotlin.StartupKt")
        }
    }

    register("serviceLoaderIjJar", ShadowJar::class) {
        isZip64 = true
        archiveBaseName.set("startup-benchmarks-serviceLoader-ij")
        from(sourceSets.named("serviceLoader").get().output)
        configurations = project.configurations.named("serviceLoaderRuntimeClasspath").map { listOf(it) }
        configurations.addAll(project.configurations.named("ij").map { listOf(it) })
        manifest {
            attributes("Main-Class" to "org.jetbrains.kotlin.StartupKt")
        }
    }

    register("mainSlimTtmJar", ShadowJar::class) {
        isZip64 = true
        archiveBaseName.set("startup-benchmarks-main-ttm-slim")
        from(sourceSets.main.get().output)
        configurations = project.configurations.named("runtimeClasspath").map { listOf(it) }
        manifest {
            attributes("Main-Class" to "org.jetbrains.kotlin.TimeUntilMainKt")
        }
    }

    register("mainIjTtmJar", ShadowJar::class) {
        isZip64 = true
        archiveBaseName.set("startup-benchmarks-main-ttm-ij")
        from(sourceSets.main.get().output)
        configurations = project.configurations.named("runtimeClasspath").map { listOf(it) }
        configurations.addAll(project.configurations.named("ij").map { listOf(it) })
        manifest {
            attributes("Main-Class" to "org.jetbrains.kotlin.TimeUntilMainKt")
        }
    }

    register("serviceLoaderSlimTtmJar", ShadowJar::class) {
        isZip64 = true
        archiveBaseName.set("startup-benchmarks-serviceLoader-ttm-slim")
        from(sourceSets.named("serviceLoader").get().output)
        configurations = project.configurations.named("serviceLoaderRuntimeClasspath").map { listOf(it) }
        manifest {
            attributes("Main-Class" to "org.jetbrains.kotlin.TimeUntilMainKt")
        }
    }

    register("serviceLoaderIjTtmJar", ShadowJar::class) {
        isZip64 = true
        archiveBaseName.set("startup-benchmarks-serviceLoader-ttm-ij")
        from(sourceSets.named("serviceLoader").get().output)
        configurations = project.configurations.named("serviceLoaderRuntimeClasspath").map { listOf(it) }
        configurations.addAll(project.configurations.named("ij").map { listOf(it) })
        manifest {
            attributes("Main-Class" to "org.jetbrains.kotlin.TimeUntilMainKt")
        }
    }

    withType(ShadowJar::class.java).configureEach {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }

    assemble.configure {
        dependsOn("mainSlimJar", "mainIjJar", "serviceLoaderIjJar", "serviceLoaderSlimJar")
    }
}
