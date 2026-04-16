plugins {
    id("com.android.application")
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("com.google.dagger.hilt.android")
}

apply(from = "../app/coverage.gradle.kts")

android {
    namespace = "com.iceteaviet.fastfoodfinder"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.iceteaviet.fastfoodfinder"

        minSdk = 28
        targetSdk = 36

        versionCode = 24
        versionName = "26.03.02"

        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
        aidl = true
    }

    lint {
        abortOnError = false
        // Disable lint running as part of release builds.
        // The lintVitalAnalyzeProdRelease task crashes due to a known bug in the
        // Kotlin Analysis API (KaFirScriptSymbol) when analyzing .kts build scripts.
        // See: https://issuetracker.google.com/issues/kotlin-lint-kts
        checkReleaseBuilds = false
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        getByName("release") {
            //isMinifyEnabled = true
            /*proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )*/
        }
    }

    flavorDimensions += "environment"

    productFlavors {
        create("mock") {
            dimension = "environment"
            // applicationIdSuffix = ".mock" // Firebase API key restricts package name
        }
        create("prod") {
            dimension = "environment"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed", /*"started", "standardOut", "standardError"*/)
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Project modules
    implementation(project(":core:network"))
    implementation(project(":core:worker"))
    implementation(project(":core:analytics"))
    implementation(project(":core:logger"))
    implementation(project(":core:common"))
    implementation(project(":core:location"))
    implementation(project(":core:notifications"))
    implementation(project(":data:model"))
    implementation(project(":data:database"))
    implementation(project(":data:datastore"))
    implementation(project(":data:stores"))

    // Local unit tests
    testImplementation(libs.bundles.local.unit.test)

    // Android UI tests
    androidTestImplementation(libs.bundles.android.ui.test)

    // Kotlin
    implementation(libs.kotlin.stdlibjdk)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // AndroidX
    implementation(libs.appcompat)
    implementation(libs.cardview)
    implementation(libs.constraintlayout)
    implementation(libs.work.runtime.ktx)

    // MVVM & Lifecycles
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)

    // Google Play Services
    implementation(libs.play.services.maps)
    implementation(libs.play.services.auth)
    implementation(libs.android.maps.utils)

    // Firebase
    implementation(libs.firebase.core)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.crashlytics)

    // UI
    implementation(libs.material)
    implementation(libs.glide)
    implementation(libs.circleimageview)

    // REST api
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Logging
    implementation(libs.timber)

    // DB
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
