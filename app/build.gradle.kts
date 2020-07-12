plugins {
    id("com.android.application")
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("realm-android")
    id("org.sonarqube") version "5.1.0.4882"
}

apply(from = "../app/coverage.gradle.kts")

android {
    namespace = "com.iceteaviet.fastfoodfinder"

    compileSdk = 35

    defaultConfig {
        applicationId = "com.iceteaviet.fastfoodfinder"

        minSdk = 28
        targetSdk = 35

        versionCode = 21
        versionName = "25.03.01"

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

    flavorDimensions += "prod"

    productFlavors {
        create("mock") {
            dimension = "mock"
            applicationIdSuffix = ".mock"
        }
        create("prod") {
            // No specific configuration needed for prod
        }
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    testLogging {
        // always show the result of every unit test, even if it passes.
        events("passed", "skipped", "failed", /*"started", "standardOut", "standardError"*/)
        //showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

sonarqube {
    // /build folder
    val buildFolder = layout.buildDirectory.get()

    properties {
        // SonarCloud authentication
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "nhoxbypass")
        property("sonar.projectKey", "nhoxbypass_FastFoodFinder")
        property("sonar.projectName", "FastFoodFinder")
        property("sonar.token", System.getenv("SONAR_TOKEN"))

        // Github branch
        property("sonar.branch.name", System.getenv("GITHUB_HEAD_REF") ?: System.getenv("GITHUB_REF_NAME") ?: "main")

        // default build variant
        property("sonar.androidVariant", "prodRelease")

        // point to sources folders
        property("sonar.sources", "src/main/java")
        // point to test folders
        property("sonar.tests", "src/test/java")
        // exclusions for non-source files
        property(
            "sonar.exclusions",
            """
            **/build/**,
            src/androidTest/**,
            src/main/res/**,
            src/main/AndroidManifest.xml,
            src/main/assets/**,
            src/prod/java/**
            """.trimIndent()
        )
        // point to compiled classes
        property("sonar.java.binaries", "$buildFolder/intermediates/runtime_app_classes_jar/prodRelease/bundleProdReleaseClassesToRuntimeJar/classes.jar")

        // include JaCoCo test report (if available)
        property("sonar.coverage.jacoco.xmlReportPaths", "$buildFolder/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
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

    //// App dependencies
    // AndroidX
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.work:work-rxjava2:2.10.0")

    // Google Play Services
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.maps.android:android-maps-utils:0.5")

    // Firebase
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth:23.2.0")
    implementation("com.google.firebase:firebase-perf:21.0.4")
    implementation("com.google.firebase:firebase-crashlytics:19.4.1")

    // UI
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // REST api
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Code flow
    implementation("org.greenrobot:eventbus:3.3.1")

    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxjava:3.1.5")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    // DB
    implementation("io.realm:realm-android-library:10.17.0")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")
}