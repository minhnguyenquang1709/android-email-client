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
    implementation("com.google.android.gms:play-services-auth:20.7.0")  // Google Sign-In
    implementation("com.google.api-client:google-api-client-android:1.33.0")  // Google API Client
    implementation("com.google.api-client:google-api-client-gson:1.33.0")  // Gson support for Google APIs
    implementation("com.google.apis:google-api-services-gmail:v1-rev110-1.25.0")  // Gmail API
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")
}
