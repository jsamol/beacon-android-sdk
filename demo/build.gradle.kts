plugins {
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "it.airgap.beaconsdkdemo"
    compileSdk = 33

    defaultConfig {
        applicationId = "it.airgap.beaconsdkdemo"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xcontext-receivers")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kapt {
       correctErrorTypes = true
    }
}

dependencies {
    implementation(Dependencies.kotlinStdlib)

    implementation(Dependencies.androidxCore)

    // Compose
    implementation(Dependencies.androidxActivityCompose)
    implementation(Dependencies.androidxComposeUi)
    implementation(Dependencies.androidxComposeUiToolingPreview)
    implementation(Dependencies.androidxComposeMaterial)

    implementation(Dependencies.androidxConstraintLayoutCompose)
    implementation(Dependencies.androidxNavigationCompose)

    // Lifecycle
    implementation(Dependencies.androidxLifecycleViewModel)
    implementation(Dependencies.androidxLifecycleRuntime)

    // Hilt
    implementation(Dependencies.hilt)
    kapt(Dependencies.hiltAndroidCompiler)

    // Serialization
    implementation(Dependencies.kotlinxSerializationJson)

    // Tezos SDK
    implementation(Dependencies.tezos)

    // Test
    testImplementation(TestDependencies.junit)

    androidTestImplementation(TestDependencies.androidxJunit)
    androidTestImplementation(TestDependencies.androidxEspresso)
    androidTestImplementation(TestDependencies.androidxComposeUiTestJunit4)
    
    debugImplementation(TestDependencies.androidxComposeUiTooling)
    debugImplementation(TestDependencies.androidxComposeUiTestManifest)
}