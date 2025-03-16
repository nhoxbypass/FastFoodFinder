plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("realm-android")
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
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
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

    val buildParam = providers.gradleProperty("build").getOrElse("")
    androidComponents {
        beforeVariants(selector().all()) { variant ->
            when (buildParam) {
                "devCI" -> {
                    if (variant.flavorName != "mock" || variant.buildType != "debug") {
                        variant.enable = false
                    }
                }

                "releaseCI" -> {
                    if (variant.flavorName != "prod" || variant.buildType != "release") {
                        variant.enable = false
                    }
                }

                else -> {
                    if (variant.buildType == "release" && variant.flavorName == "mock") {
                        variant.enable = false
                    }
                }
            }
        }
    }
}

// Always show the result of every unit test, even if it passes.
tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed", "started", "standardOut", "standardError")
        showStandardStreams = true
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

    // Dependencies for local unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
    testImplementation("org.assertj:assertj-core:3.11.1")
    //testImplementation("com.google.truth:truth:0.44") // Does not stable
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    // Espresso UI Testing dependencies.
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.6.1")

    // Android Testing Library's runner and rules
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    implementation("androidx.test.espresso:espresso-idling-resource:3.6.1")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    //// App dependencies
    // AndroidX
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.work:work-rxjava2:2.10.0")

    // Google Play Services
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
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

kapt {
    generateStubs = true
}