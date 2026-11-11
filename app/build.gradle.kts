plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    //alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.ksp)     // Enable KSP
    alias(libs.plugins.hilt.android)   // Enable Hilt
    alias(libs.plugins.androidx.room)   // Enable Hilt
}

android {
    namespace = "com.saifee.inventorysalesmanagement"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.saifee.inventorysalesmanagement"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = false
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
    }

}
room {
    schemaDirectory("$projectDir/schemas")
}
dependencies {
    implementation(project(":barcode-scanner"))
    //Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.viewbinding)
    implementation(libs.androidx.appcompat)

    //Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.material.icons)

    //Work-Manager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.camera.core)
    implementation("androidx.biometric:biometric:1.2.0-alpha05")
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)

    // Security
    implementation(libs.androidx.security.crypto)


    // Coroutines
    implementation(libs.coroutines.ktx)

    // Room & Paging
    implementation(libs.room.ktx)
    implementation("androidx.room:room-paging:2.8.5")
    implementation("androidx.paging:paging-runtime-ktx:3.3.6")
    implementation("androidx.paging:paging-compose:3.3.6")
    ksp(libs.room.compiler)           // KSP instead of kapt

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.compiler)           // KSP instead of kapt

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)

    //retrofit
    implementation(libs.okhttp3.okhttp)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.okhttp.urlconnection)
    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.retrofit2.converter.scalars)
    //Coil- Image Library
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
