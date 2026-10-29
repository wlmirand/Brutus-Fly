import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "william.miranda.brutusfly.core.ui"
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        minSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}
