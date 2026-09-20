plugins {
    alias(libs.plugins.android.application)
    // Plugin Google Services: lee google-services.json y genera recursos Firebase
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.asistencia"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.asistencia"
        minSdk = 34
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

    // ViewBinding: genera clases tipo-seguras para cada layout XML
    // Funciona perfectamente con Java — elimina todos los findViewById()
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // ViewModel + LiveData: patrón MVVM compatible con Java
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Firebase: el BoM garantiza versiones compatibles entre módulos
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.rtdb)   // Realtime Database

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}