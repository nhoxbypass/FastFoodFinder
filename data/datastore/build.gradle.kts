plugins {
    id("com.android.library")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.iceteaviet.fastfoodfinder.data.datastore"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Local unit tests
    testImplementation(libs.bundles.local.unit.test)
}