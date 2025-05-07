import com.android.build.gradle.internal.api.LibraryVariantOutputImpl
import org.jetbrains.kotlin.ir.backend.js.compile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.library)
    id("com.kezong.fat-aar")
//    id("maven-publish")
}

android {
    namespace = "com.unionware"
    compileSdk = 34

//    publishNonDefault = true
    defaultConfig {
        minSdk = 24
    }
    buildToolsVersion = "34.0.0"
}

dependencies {
//    compile(project(":baselib"))
//    api(project(":MES"))
//    api(project(":EMES"))
//    api(project(":MES_Process"))
//    api(project(":ONCE"))
//    api(project(":WMS"))
//    api(project(":QUERY"))
//    embed(project(":baselib",configuration = "default"))
    embed(project(":basicui",configuration = "default"))
    embed(project(":MES",configuration = "default"))
//    embed(project(":EMES"))
//    embed(project(":MES_Process"))
//    embed(project(":ONCE"))
//    embed(project(":WMS"))

//    api(fileTree("libs") {
//        include("*.aar")
//    })

//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
//    implementation(libs.androidx.activity)
//    implementation(libs.androidx.constraintlayout)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//
//    implementation(libs.dagger.hilt.android)
}/*
afterEvaluate {
    publishing {
        publications {
            register("release", MavenPublication::class) {
//                from(components["release"])
                artifactId = "unionware"
                groupId = "com.unionware.lib"
                version = "1.0.0"
//                artifact(tasks["bundleReleaseAar"])
//                artifact(uri("${rootDir}/maven/app-debug.aar"))
                //E:\AndroidApp\unionware\baselib\build\outputs\aar
//                artifact(project(":basicui").project)
//                artifact(uri("E:\\AndroidApp\\unionware\\baselib\\build\\outputs\\aar\\baselib-release.aar"))
//                artifact(uri("E:\\AndroidApp\\unionware\\basicui\\build\\outputs\\aar\\basicui-release.aar"))
            }
        }
        repositories {
            maven {
                url = uri("${rootDir}/maven") // rootDir为项目根目录，maven为存放maven库的文件夹
            }
        }
    }
}*/
