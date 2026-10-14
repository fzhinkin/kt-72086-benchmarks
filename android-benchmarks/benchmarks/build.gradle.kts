plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.benchmark)
}

android {
    namespace = "org.example.benchmark"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    }
    lint {
        targetSdk = 36
    }
    testOptions {
        targetSdk = 36
    }
    testBuildType = "release"
    buildTypes {

        debug {
            // Since isDebuggable can"t be modified by gradle for library modules,
            // it must be done in a manifest - see src/androidTest/AndroidManifest.xml
            isMinifyEnabled = true
        }
        release {
            isMinifyEnabled = true
            isDefault = true
            androidTest.enableMinification = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.benchmark.ExperimentalBlackHoleApi",
            "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.ObsoleteCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
        )
    }
}

dependencies {
    androidTestImplementation(libs.androidx.benchmark.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.kotlinx.coroutines.core)
}
