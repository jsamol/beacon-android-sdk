object Android {
    const val compileSdk = 33
    const val minSdk = 21
    const val targetSdk = 33

    const val versionCode = 31
    const val versionName = "3.2.3"
}

object Version {
    const val kotlin = "1.7.20"

    const val kotlinSerialization = "1.3.1"

    const val androidxCore = "1.9.0"
    const val androidxAppCompat = "1.3.1"
    const val androidxConstraintLayout = "2.1.0"
    const val androidxConstraintLayoutCompose = "1.0.1"

    const val androidxActivity = "1.6.1"
    const val androidxFragment = "1.5.4"
    const val androidxComposeUi = "1.3.1"
    const val androidxComposeMaterial = "1.3.1"
    const val androidxLifecycle = "2.5.1"
    const val androidxNavigation = "2.5.3"

    const val androidxSecurity = "1.0.0"

    const val coroutines = "1.5.1"

    const val hilt = "2.44"

    const val ktor = "1.6.2"

    const val lazySodium = "5.0.2"
    const val jna = "5.9.0"

    const val materialComponents = "1.4.0"

    const val tezos = "0.0.6-beta01"

    const val junit = "4.13.2"

    const val androidxJunit = "1.1.4"
    const val androidxEspresso = "3.5.0"

    const val mockk = "1.12.0"

    const val bouncyCastle = "1.70"
}

object Dependencies {
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}"
    const val kotlinReflection = "org.jetbrains.kotlin:kotlin-reflect:${Version.kotlin}"

    const val kotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.kotlinSerialization}"
    const val kotlinxCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutines}"
    const val kotlinxCoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.coroutines}"

    const val ktorOkHttp = "io.ktor:ktor-client-okhttp:${Version.ktor}"
    const val ktorJson = "io.ktor:ktor-client-json:${Version.ktor}"
    const val ktorSerializationJvm = "io.ktor:ktor-client-serialization-jvm:${Version.ktor}"
    const val ktorLoggingJvm = "io.ktor:ktor-client-logging-jvm:${Version.ktor}"

    const val androidxCore = "androidx.core:core-ktx:${Version.androidxCore}"

    const val androidxAppCompat = "androidx.appcompat:appcompat:${Version.androidxAppCompat}"

    const val androidxConstraintLayout = "androidx.constraintlayout:constraintlayout:${Version.androidxConstraintLayout}"
    const val androidxConstraintLayoutCompose = "androidx.constraintlayout:constraintlayout-compose:${Version.androidxConstraintLayoutCompose}"

    const val androidxActivity = "androidx.activity:activity-ktx:${Version.androidxActivity}"
    const val androidxActivityCompose = "androidx.activity:activity-compose:${Version.androidxActivity}"
    const val androidxFragment = "androidx.fragment:fragment-ktx:${Version.androidxFragment}"

    const val androidxComposeUi = "androidx.compose.ui:ui:${Version.androidxComposeUi}"
    const val androidxComposeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Version.androidxComposeUi}"
    const val androidxComposeMaterial = "androidx.compose.material:material:${Version.androidxComposeMaterial}"

    const val androidxLifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.androidxLifecycle}"
    const val androidxLifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Version.androidxLifecycle}"
    const val androidxLifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Version.androidxLifecycle}"

    const val androidxNavigationFragment = "androidx.navigation:navigation-fragment-ktx:${Version.androidxNavigation}"
    const val androidxNavigationUi = "androidx.navigation:navigation-ui-ktx:${Version.androidxNavigation}"
    const val androidxNavigationCompose = "androidx.navigation:navigation-compose:${Version.androidxNavigation}"

    const val androidxSecurity = "androidx.security:security-crypto:${Version.androidxSecurity}"

    const val hilt = "com.google.dagger:hilt-android:${Version.hilt}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Version.hilt}"

    const val materialComponents = "com.google.android.material:material:${Version.materialComponents}"

    const val lazySodium = "com.goterl:lazysodium-android:${Version.lazySodium}@aar"
    const val jna = "net.java.dev.jna:jna:${Version.jna}@aar"

    const val tezos = "com.github.airgap-it:tezos-kotlin-sdk:${Version.tezos}"
}

object TestDependencies {
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Version.kotlin}"
    const val kotlinxCoroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Version.coroutines}"

    const val junit = "junit:junit:${Version.junit}"

    const val androidxJunit = "androidx.test.ext:junit:${Version.androidxJunit}"
    const val androidxEspresso = "androidx.test.espresso:espresso-core:${Version.androidxEspresso}"

    const val androidxComposeUiTestJunit4 = "androidx.compose.ui:ui-test-junit4:${Version.androidxComposeUi}"
    const val androidxComposeUiTooling = "androidx.compose.ui:ui-tooling:${Version.androidxComposeUi}"
    const val androidxComposeUiTestManifest = "androidx.compose.ui:ui-test-manifest:${Version.androidxComposeUi}"

    const val mockk = "io.mockk:mockk:${Version.mockk}"

    const val bouncyCastle = "org.bouncycastle:bcprov-jdk15on:${Version.bouncyCastle}"
}