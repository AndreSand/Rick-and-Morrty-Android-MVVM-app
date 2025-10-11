plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.2.0"
    id("com.google.protobuf") version "0.9.5"  // Latest version
}

android {
    namespace = "com.android.rickandmortymvvm"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.android.rickandmortymvvm"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    
    buildFeatures {
        compose = true
    }
}

// Protobuf configuration
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.28.2"  // Latest stable version
    }
    
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")  // Generate lite version
                }
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")  // Latest version

    // JSON serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    
    // Protocol Buffers - Lite version (for Android)
    implementation("com.google.protobuf:protobuf-javalite:4.28.2")  // Latest stable lite version
    
    // Retrofit Protobuf converter with exclusion to avoid conflict
    implementation("com.squareup.retrofit2:converter-protobuf:2.11.0") {
        exclude(group = "com.google.protobuf", module = "protobuf-java")  // Exclude full version
    }

    // Coil for image loading
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")

    // Unit Testing Dependencies
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
