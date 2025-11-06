plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    
    lint {
        abortOnError = false
    }
}

ksp {
    arg("dagger.fastInit", "enabled")
    arg("dagger.hilt.shareTestComponents", "true")
}

dependencies {
    // Removed domain dependency to avoid circular dependency
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Biometric authentication
    implementation("androidx.biometric:biometric:1.1.0")
    
    // Billing
    implementation(libs.play.billing)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
}


