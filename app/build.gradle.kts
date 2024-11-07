plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "vn.edu.usth.mobile_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "vn.edu.usth.mobile_project"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Add packaging options to exclude duplicate files
    packagingOptions {
        exclude ("META-INF/DEPENDENCIES")
        exclude ("META-INF/LICENSE.md")  // Exclude the LICENSE.md file
        exclude ("META-INF/NOTICE.md")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Add these dependencies for Google Sign-In and Gmail API
    implementation(libs.play.services.auth)  // Google Sign-In
    implementation(libs.google.api.client.android)  // Google API Client
    implementation(libs.google.api.client.gson)  // Gson support for Google APIs
    implementation(libs.google.api.services.gmail)  // Gmail API
    implementation (libs.android.mail)
    implementation (libs.android.activation)
}
