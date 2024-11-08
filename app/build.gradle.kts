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
    implementation(libs.googleid)
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

    // credential manager for authentication
    implementation("androidx.credentials:credentials:1.3.0")

    // optional - needed for credentials support from play services, for devices running
    // Android 13 and below.
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    // gmail api
    implementation("com.google.api-client:google-api-client:2.0.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-gmail:v1-rev20220404-2.0.0")

}
