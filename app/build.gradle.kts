plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.lupay"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lupay"
        minSdk = 29
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
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
dependencies {
    implementation (libs.material3)
    implementation (libs.androidx.navigation.compose.v250)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.activity.compose.v172)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.glance)
    implementation(libs.androidx.material3.v110)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.animation.core.lint)
    implementation(libs.androidx.material.icons.extended.v150)
    implementation(libs.androidx.material3.v100)
    implementation(libs.androidx.lifecycle.viewmodel.compose.v251)
    implementation(libs.androidx.material.icons.extended.v105)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.material3.v112)
    implementation(libs.compose)
    implementation (libs.androidx.compose.material)
    implementation (libs.androidx.compose.foundation)
    implementation (libs.androidx.compose.navigation)
    implementation( libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle.v123)
    implementation (libs.androidx.camera.view.v123)
    implementation (libs.core)
    implementation (libs.kotlinx.coroutines.android)

    implementation(libs.androidx.lifecycle.viewmodel.compose.v261)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.retrofit.v2110)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.kotlinx.serialization.json.v163)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.vision.common)
    implementation(libs.play.services.mlkit.barcode.scanning)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.converter.kotlinx.serialization)
    debugImplementation(libs.androidx.ui.test.manifest)

}