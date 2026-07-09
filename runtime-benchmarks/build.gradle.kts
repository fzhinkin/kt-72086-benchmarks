plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.benchmark") version "0.4.17"
}

kotlin {
    jvmToolchain(21)

    val main = target.compilations.getByName("main")
    val serviceLoader = target.compilations.create("serviceLoader")
    val serviceLoaderOpt = target.compilations.create("serviceLoaderOpt")
    val singleService = target.compilations.create("singleService")


    main.defaultSourceSet.kotlin.srcDir("src/main/kotlin")
    serviceLoader.defaultSourceSet.kotlin.srcDir("src/main/kotlin")
    serviceLoaderOpt.defaultSourceSet.kotlin.srcDir("src/main/kotlin")
    singleService.defaultSourceSet.kotlin.srcDir("src/main/kotlin")
}

dependencies {
    val commonDeps = listOf(
        "org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.17",
        "io.reactivex.rxjava2:rxjava:2.1.9",
        "com.github.akarnokd:rxjava2-extensions:0.20.8",
        "io.projectreactor:reactor-core:3.4.1",
        "com.typesafe.akka:akka-actor_2.12:2.5.0"
    )

    for (dep in commonDeps) {
        implementation(dep)
        "serviceLoaderImplementation"(dep)
        "serviceLoaderOptImplementation"(dep)
        "singleServiceImplementation"(dep)
    }

    api("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0")
    "serviceLoaderImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0-dpspi")
    "serviceLoaderImplementation"("org.jetbrains.kotlin:kotlin-stdlib:2.4.255-SNAPSHOT")

    "serviceLoaderOptImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0-dpspi")
    "serviceLoaderOptImplementation"("org.jetbrains.kotlin:kotlin-stdlib:2.4.255-c1-SNAPSHOT")

    "singleServiceImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0-dpspi")
    "singleServiceImplementation"("org.jetbrains.kotlin:kotlin-stdlib:2.4.255-s-SNAPSHOT")
}

benchmark {
    targets {
        create("main")
        create("serviceLoader")
        create("serviceLoaderOpt")
        create("singleService")
    }
}
