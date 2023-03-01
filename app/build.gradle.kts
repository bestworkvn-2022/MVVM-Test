import java.text.SimpleDateFormat
import java.util.Calendar
import java.io.*
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

buildscript {
    apply(from = "../ktlint.gradle.kts")
}

val keystoreProperties = rootDir.loadGradleProperties("signing.properties")

android {
    signingConfigs {
        create("release") {
            // Remember to edit signing.properties to have the correct info for release build.
            storeFile = file("../keystore/release.keystore")
            storePassword = keystoreProperties.getProperty("KEYSTORE_PASSWORD") as String
            keyPassword = keystoreProperties.getProperty("KEY_PASSWORD") as String
            keyAlias = keystoreProperties.getProperty("KEY_ALIAS") as String
        }
    }

    compileSdk = 33
    buildToolsVersion = "30.0.3"
    flavorDimensions += "default"

    defaultConfig {
        applicationId = "com.authentication.mvvm"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"
        vectorDrawables.useSupportLibrary = true
    }

    productFlavors {
        create("dev") {
            versionCode = 1
            versionName = "1.0.0"

            buildConfigField("String", "END_POINT", "\"https://pokeapi.co/api/v2/\"")
        }

        create("prod") {
            versionCode = 1
            versionName = "1.0.0"

            buildConfigField("String", "END_POINT", "\"https://pokeapi.co/api/v2/\"")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
            proguardFile("proguard/proguard-google-play-services.pro")
            proguardFile("proguard/proguard-square-okhttp.pro")
            proguardFile("proguard/proguard-square-retrofit.pro")
            proguardFile("proguard/proguard-google-analytics.pro")
            proguardFile("proguard/proguard-facebook.pro")
            proguardFile("proguard/proguard-project.pro")
            proguardFile("proguard/proguard-hilt.pro")
            proguardFile("proguard/proguard-support-v7-appcompat.pro")
            proguardFile("proguard/okhttp3.pro")
            proguardFile("proguard/kotlin.pro")
            proguardFile("proguard/retrofit2.pro")
            proguardFile("proguard/proguard-testfairy.pro")
            signingConfig = signingConfigs["release"]
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildFeatures {
        viewBinding = true
    }

    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/ASL2.0")
        resources.excludes.add("META-INF/*.kotlin_module")
    }

    applicationVariants.all {
        val outputFileName = name +
            "_versionName_$versionName" +
            "_versionCode_$versionCode" +
            "_time_${SimpleDateFormat("HH_mm_dd_MM_yyyy").format(Calendar.getInstance().time)}.apk"
        outputs.all {
            val output = this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output?.outputFileName = outputFileName
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlinx.coroutines.FlowPreview" + "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }
}

kapt {
    useBuildCache = true
    correctErrorTypes = true
}

dependencies {
    // Ktlint
    compileOnly("com.pinterest.ktlint:ktlint-core:0.46.1")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
    // App compat & design
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    // Okhttp
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.3")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")
    // Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("com.github.florent37:glidepalette:2.1.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.1")
    kapt("com.github.bumptech.glide:compiler:4.13.1")
    // Gson
    implementation("com.google.code.gson:gson:2.9.1")
    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    // Leak canary
    // debugImplementation("com.squareup.leakcanary:leakcanary-android:2.0")
    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")
    // KTX
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.5.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.activity:activity-ktx:1.8.0-alpha01")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0-rc01")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.44.2")
    kapt("com.google.dagger:hilt-android-compiler:2.44.2")
    // Lottie
    implementation("com.airbnb.android:lottie:5.2.0")
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // Paging
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:31.1.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.android.gms:play-services-auth:20.4.1")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    // FacebookSDK
    implementation("com.facebook.android:facebook-login:latest.release")
    implementation("com.facebook.android:facebook-share:latest.release")
}

fun File.loadGradleProperties(fileName: String): Properties {
    val properties = Properties()
    val signingProperties = File(this, fileName)

    if (signingProperties.isFile) {
        properties.load(signingProperties.inputStream())
    }
    return properties
}