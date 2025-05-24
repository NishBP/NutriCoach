// File: app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // Kotlin Annotation Processing Tool
    id("org.jetbrains.kotlin.plugin.compose") // Compose Compiler
}

android {
    namespace = "com.fit2081.nishal34715231"
    compileSdk = 35 // Consider aligning with targetSdk (34) or ensure stability of SDK 35
    // For stability, you might prefer compileSdk = 34 if targetSdk is 34

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
    // Assuming your libs.versions.toml [versions] has:
    // coreKtx = "1.15.0" (or the version you intend for core-ktx)
    // lifecycleRuntimeKtx = "2.8.7" (or the version for lifecycle-runtime-ktx)
    // activityCompose = "1.10.1" (or the version for activity-compose)
    // composeBom = "2024.04.01" (this is the actual BOM version string)
    // junit = "4.13.2"
    // androidxJunit = "1.2.1" (for androidx.test.ext:junit)
    // espressoCore = "3.6.1" (for androidx.test.espresso:espresso-core)
    // roomVersion = "2.6.1"
    // lifecycleVersion = "2.7.0" (or newer, e.g., "2.8.7" if compatible for all lifecycle components)
    // kotlinxCoroutinesCore = "1.7.3"
    // kotlinxCoroutinesAndroid = "1.7.3"
    // gson = "2.10.1"

    // And your libs.versions.toml [libraries] section defines aliases like:
    // androidx-core-ktx = { module = "androidx.core:core-ktx", version.ref = "coreKtx" }
    // androidx-lifecycle-runtime-ktx = { module = "androidx.lifecycle:lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
    // androidx-activity-compose = { module = "androidx.activity:activity-compose", version.ref = "activityCompose" }
    // androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
    // androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
    // etc.

    implementation(libs.androidx.core.ktx) // Corrected: e.g., from libs.androidx.core.ktx.v1120
    implementation(libs.androidx.lifecycle.runtime.ktx) // Corrected: e.g., from libs.androidx.lifecycle.runtime.ktx.v270
    implementation(libs.androidx.activity.compose) // Corrected: e.g., from libs.androidx.activity.compose.v182

    implementation(platform(libs.androidx.compose.bom)) // Corrected: e.g., from libs.androidx.compose.bom.v20250501
    // Use platform() for BOMs
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit) // Corrected: e.g., from libs.androidx.junit.v115
    androidTestImplementation(libs.androidx.test.espresso.core) // Corrected: e.g., from libs.androidx.espresso.core.v351
    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM for test dependencies
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Room components
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Lifecycle components for ViewModel and LiveData/Flow
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Gson for Type Converters
    implementation(libs.google.gson) // Assuming alias is 'google-gson' or similar for com.google.code.gson:gson
    // If your alias is just 'gson', then libs.gson is correct.
}
