plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.wear"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.wear"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //librerias
    implementation ("androidx.wear.compose:compose-material:1.4.0") // Wear Compose Material
    implementation ("androidx.compose.material:material:1.4.3") // Compose Material estándar
    implementation ("androidx.compose.ui:ui:1.4.3") // Compose UI core
    implementation ("androidx.compose.ui:ui-tooling-preview:1.4.3") // Preview (opcional)
    implementation ("androidx.activity:activity-compose:1.7.2")
    implementation ("androidx.wear.compose:compose-foundation:1.4.0")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.wear.compose:compose-material:1.4.0")
    //estos dos son los ultimos que agregue
    implementation ("androidx.compose.material:material-icons-extended:1.6.0")

}