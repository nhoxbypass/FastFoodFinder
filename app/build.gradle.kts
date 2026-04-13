plugins {
    id("com.android.application")
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("org.sonarqube") version "7.2.3.7755"
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
            applicationIdSuffix = ".mock"
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

sonarqube {
    val buildFolder = layout.buildDirectory.get()
    val sonarToken = System.getenv("SONAR_TOKEN")
        ?: file("${rootProject.projectDir}/local.properties")
            .let { if (it.exists()) it.readLines().find { l -> l.startsWith("SONAR_TOKEN=") }?.substringAfter("=") else null }
        ?: ""

    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "nhoxbypass")
        property("sonar.projectKey", "nhoxbypass_FastFoodFinder")
        property("sonar.projectName", "FastFoodFinder")
        property("sonar.token", sonarToken)

        property("sonar.branch.name", System.getenv("GITHUB_HEAD_REF") ?: System.getenv("GITHUB_REF_NAME") ?: "main")

        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
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
        property("sonar.java.binaries", "$buildFolder/intermediates/runtime_app_classes_jar/prodDebug/bundleProdDebugClassesToRuntimeJar/classes.jar")

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
