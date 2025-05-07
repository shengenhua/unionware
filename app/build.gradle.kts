import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hint.android)
//    id("com.kezong.fat-aar")
    kotlin("kapt")
}

kapt {
    correctErrorTypes = true
}
android {
    signingConfigs {
        this.create("release") {
            storeFile = file("E:\\AndroidApp\\unionware\\app\\unionware_mes.jks")
            storePassword = "uNionwAre@76"
            keyAlias = "unionware"
            keyPassword = "winkoo"
        }
    }

    namespace = "com.unionware"
    compileSdk = 34

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    defaultConfig {
        applicationId = "com.unionware"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = getSelfDefinedVersion()//"1.1.001"
//        versionName = "1.1.001"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        flavorDimensions.add("mes")
        signingConfig = signingConfigs.getByName("release")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            //noinspection GradlePath
            applicationIdSuffix = "debug"
//            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("release")
        }
    }
    buildFeatures {
        viewBinding = true
        this.dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    android.applicationVariants.all {
        val date = SimpleDateFormat("yyyyMMddHHmm").format(Date())
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                this.outputFileName =
                    "轻优联${(flavorName ?: project.name).uppercase(Locale.getDefault())}【星空版】_${versionName}_${date}.apk"
            }
        }
    }

    productFlavors {
        /*create("debug") {
            manifestPlaceholders.putAll(mapOf("appName" to "轻优联【星空版】Debug"))
        }*/
        create("unionware") {
            manifestPlaceholders.putAll(mapOf("appName" to "轻优联【星空版】"))
        }
        create("mes") {
            applicationIdSuffix = ".mes"
//            versionNameSuffix = "-stargate"
            manifestPlaceholders.putAll(mapOf("appName" to "轻优联MES【星空版】"))
        }
        create("nuoan") {
            applicationIdSuffix = ".mes"
            manifestPlaceholders.putAll(mapOf("appName" to "诺安智慧MES"))
        }
        create("zhoyu") {
            applicationIdSuffix = ".mes"
            manifestPlaceholders.putAll(mapOf("appName" to "中裕轻MES"))
        }
        create("dongbao") {
            applicationIdSuffix = ".mes"
            manifestPlaceholders.putAll(mapOf("appName" to "东宝轻MES【星空版】"))
        }
    }
}

/**
 * 主版本号(Major Version)‌：表示主要的版本更新，如功能模块的重大变化。
 * ‌子版本号(Minor Version)‌：表示次要更新，如新增功能或优化现有功能。
 * ‌阶段版本号‌：表示开发阶段，如alpha、beta、RC（Release Candidate）等。
 * ‌日期版本号‌：记录当前的修改日期。
 * ‌希腊字母版本号‌：用于标识不同的开发阶段，如alpha、beta、RC、release等‌
 */
fun getSelfDefinedCode(): Int {
    val major = 1
    val minor = 1
    val revision = Integer.parseInt(getSvnRevision())
    return major * 10000000 + minor * 1000000 + revision * 10000
//    return "1.1.${getSvnRevision()}"
}

fun getSelfDefinedVersion(): String {
    return "1.1.${getSvnRevision()}"
}

fun getSvnRevision(): String {
    Runtime.getRuntime().exec("svn info --show-item revision").inputStream.bufferedReader().use {
        return it.readLine()
    }
}

dependencies {
//    implementation(fileTree("libs") {
//        include("*.aar", "*.jar")
//    })
//    implementation(fileTree("libs") {
//        include("*.aar")
//    })
//    implementation(project(":unionwareLib"))
    implementation(project(":baselib"))
    implementation(project(":MES"))
    implementation(project(":EMES"))
    implementation(project(":MES_Process"))
    implementation(project(":ONCE"))
    implementation(project(":WMS"))
    implementation(project(":QUERY"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)
//    implementation(libs.dagger.hilt.android)
//    kapt(libs.androidx.room.compiler)

//    implementation(libs.dagger.hilt.android)
//    kapt(libs.androidx.room.compiler)
}