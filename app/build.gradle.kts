// File: app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Kotlin Annotation Processing Tool
    id("org.jetbrains.kotlin.plugin.compose") // Compose Compiler
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.fit2081.nishal34715231"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fit2081.nishal34715231"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Add your Gemini API key here
            buildConfigField("String", "GEMINI_API_KEY", "\"AIzaSyCYD6hNRMNsM78g9IQeBWV3DodKHj8Z6J4\"")
        }
        debug {
            // Add your Gemini API key here
            buildConfigField("String", "GEMINI_API_KEY", "\"AIzaSyCYD6hNRMNsM78g9IQeBWV3DodKHj8Z6J4\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true // Enable BuildConfig generation for API key
    }
    composeOptions {
        // kotlinCompilerExtensionVersion is managed by the Compose plugin and BOM
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.compose.runtime.livedata)
    implementation("androidx.navigation:navigation-compose:2.9.0") // Using a known stable version
    // Core Android libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM - use platform() for BOMs
    implementation(platform(libs.androidx.compose.bom))

    // Compose UI libraries (these don't need version numbers due to BOM)
    implementation(libs.ui)  // Changed from libs.androidx.compose.ui
    implementation(libs.ui.graphics)  // Changed from libs.androidx.compose.ui.graphics
    implementation(libs.ui.tooling.preview)  // Changed from libs.androidx.compose.ui.tooling.preview
    implementation(libs.material3)  // Changed from libs.androidx.compose.material3

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)  // This matches your toml file
    androidTestImplementation(libs.androidx.espresso.core)  // This matches your toml file
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)  // Changed from libs.androidx.compose.ui.test.junit4

    // Debug implementations
    debugImplementation(libs.ui.tooling)  // Changed from libs.androidx.compose.ui.tooling
    debugImplementation(libs.ui.test.manifest)  // Changed from libs.androidx.compose.ui.test.manifest

    // Room components
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Lifecycle components for ViewModel and LiveData/Flow
    implementation(libs.lifecycle.viewmodel.ktx)  // Changed from libs.androidx.lifecycle.viewmodel.ktx
    implementation(libs.lifecycle.livedata.ktx)  // Changed from libs.androidx.lifecycle.livedata.ktx
    implementation(libs.lifecycle.runtime.compose)  // Changed from libs.androidx.lifecycle.runtime.compose

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Gson for Type Converters
    implementation(libs.gson)

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Google Gemini AI
    implementation("com.google.ai.client.generativeai:generativeai:0.2.1")
}