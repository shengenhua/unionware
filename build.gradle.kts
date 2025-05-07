buildscript {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.github.aasitnikov:fat-aar-android:1.4.2")
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.dagger.hint.android) apply false
}