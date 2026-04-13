// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    dependencies {
        classpath(libs.gradle)

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
        classpath(libs.google.services.classpath)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.firebase.perf.plugin)

        classpath(libs.jacoco.core)


        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.android.gradle.plugin)
        classpath("org.apache.commons:commons-compress:1.27.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}

plugins {
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    id("org.sonarqube") version "7.2.3.7755"
}

sonarqube {
    val buildFolder = project(":app").layout.buildDirectory.get()
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

        property("sonar.sources", "app/src/main/java")
        property("sonar.tests", "app/src/test/java")
        property(
            "sonar.exclusions",
            """
            **/build/**,
            app/src/androidTest/**,
            app/src/main/res/**,
            app/src/main/AndroidManifest.xml,
            app/src/main/assets/**,
            app/src/prod/java/**
            """.trimIndent()
        )
        property("sonar.java.binaries", "$buildFolder/intermediates/runtime_app_classes_jar/prodDebug/bundleProdDebugClassesToRuntimeJar/classes.jar")

        property("sonar.coverage.jacoco.xmlReportPaths", "$buildFolder/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        property("sonar.gradle.skipCompile", "true")
    }
}

subprojects {
    sonarqube {
        isSkipProject = true
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
