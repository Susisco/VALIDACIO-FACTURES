// app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.kapt") // ✅ En Kotlin DSL el id es este, no "kotlin-kapt"

}

android {
    namespace = "com.ajterrassa.validaciofacturesalbarans"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ajterrassa.validaciofacturesalbarans"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // Esto debe coincidir con "androidx.compose.ui:ui:1.5.4" más abajo
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- Compose & UI (mantinc versions que tens) ---
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.ui:ui-text:1.5.4")
    implementation("androidx.compose.foundation:foundation:1.5.4")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation ("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // --- Xarxa & JSON (mantinc 2.9.0 per compatibilitat amb el teu codi) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // --- Coroutines ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // --- Seguretat (SharedPreferences xifrades) ---
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // --- 🔹 NOVETATS: Offline-first ---
    // Room (cua local)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // WorkManager (reintents en segon pla)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // ML Kit: reconeixement de text (validació de foto)
    implementation("com.google.mlkit:text-recognition:16.0.1")

    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-installations-ktx")
    implementation("com.google.android.play:integrity:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    // --- Debug / Tests (els que ja tens) ---
    debugImplementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")

    implementation ("io.coil-kt:coil-compose:2.6.0")
    // (opcional) si vols també la base no-compose:
    // implementation "io.coil-kt:coil:2.6.0"
}

