import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

fun gitBranch(): String {
    return try {
        val byteOut = ByteArrayOutputStream()
        project.exec {
            commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
            standardOutput = byteOut
        }
        String(byteOut.toByteArray()).trim()
    } catch (e: Exception) {
        "unknown"
    }
}

fun gitHash(): String {
    return try {
        val byteOut = ByteArrayOutputStream()
        project.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
            standardOutput = byteOut
        }
        String(byteOut.toByteArray()).trim()
    } catch (e: Exception) {
        "unknown"
    }
}

fun gitCommitDate(): String {
    return try {
        val byteOut = ByteArrayOutputStream()
        project.exec {
            commandLine("git", "log", "-1", "--format=%ai")
            standardOutput = byteOut
        }
        String(byteOut.toByteArray()).trim()
    } catch (e: Exception) {
        "unknown"
    }
}

android {
    namespace = "com.lanrhyme.shardlauncher"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lanrhyme.shardlauncher"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        val vName = "1.0"
        versionName = vName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val isRelease = project.hasProperty("isReleaseBuild")
        val buildStatus = if (isRelease) "正式版" else "开发版"

        resValue("string", "git_branch", gitBranch())
        resValue("string", "git_hash", gitHash())
        resValue("string", "version_name", vName)
        resValue("string", "last_update_time", gitCommitDate())
        resValue("string", "build_status", buildStatus)

        buildConfigField("String", "CLIENT_ID", "\"${System.getenv("CLIENT_ID") ?: ""}\"")
    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            isUniversalApk = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("com.google.android.material:material:1.12.0") // Add this for M3 themes
    implementation("androidx.navigation:navigation-compose:2.9.4")
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.androidx.compose.foundation.layout)
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("androidx.media3:media3-common:1.3.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
