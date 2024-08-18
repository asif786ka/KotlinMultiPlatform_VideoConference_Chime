plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization) // Added for Kotlinx Serialization support
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Your multiplatform dependencies here
                implementation(libs.ktor.core)
                implementation(libs.ktor.json)
                implementation(libs.ktor.serialization)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kermit)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.android)
            }
        }
        // Ensure iosMain source set is correctly referenced
        val iosMain by creating {
            dependencies {
                implementation(libs.ktor.client.ios)
            }
        }

    }
}

android {
    namespace = "com.asif.kmmvideoconference"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.android)
    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-serialization:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("software.aws.chimesdk:amazon-chime-sdk:0.23.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.3")
}
