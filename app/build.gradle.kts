import java.io.FileInputStream
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")!!

if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.miniproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.miniproject"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Helper to get property without quotes and spaces
        fun getLocalProperty(key: String): String {
            return localProperties.getProperty(key)?.replace("\"", "")?.trim() ?: ""
        }

        buildConfigField(
            "String",
            "TWILIO_ACCOUNT_SID",
            "\"${getLocalProperty("TWILIO_ACCOUNT_SID")}\""
        )

        buildConfigField(
            "String",
            "TWILIO_AUTH_TOKEN",
            "\"${getLocalProperty("TWILIO_AUTH_TOKEN")}\""
        )

        buildConfigField(
            "String",
            "VERIFY_SERVICE_SID",
            "\"${getLocalProperty("VERIFY_SERVICE_SID")}\""
        )

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

    buildFeatures{
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.fragment)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.cardview)
    implementation(libs.firebase.database)
    implementation(libs.recyclerview)

    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    
    // External Libraries using the correct Version Catalog references
    implementation(libs.imageslideshow)
    implementation(libs.play.services.maps)
    implementation(libs.gms.play.services.location)
    implementation(libs.firebase.ui.database)
    implementation(libs.glide)
}
