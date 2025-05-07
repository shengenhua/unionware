import com.android.build.gradle.internal.api.LibraryVariantOutputImpl
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hint.android)
    id("com.kezong.fat-aar")
    kotlin("kapt")
}
kapt {
    correctErrorTypes = true
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}

android {
    namespace = "com.unionware.mes"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildToolsVersion = "34.0.0"
    fataar {
        this.transitive = true
    }
//    libPack()
// 输出类型
//    android.libraryVariants.all {
//        // 编译类型
////        val buildType = this.buildType.name
//        val date = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
//        outputs.all {
//            // 判断是否是输出 apk 类型
//            if (
////                this is com.android.build.gradle.internal.api.ApkVariantOutputImpl
//                this is com.android.build.gradle.internal.api.LibraryVariantOutputImpl
//            ) {
//                val buildDir = project.rootProject.rootDir
////                var outputDir = File(buildDir, "libs")
//                this.outputFileName = "${project.name}_${date}.aar"
//            }
//        }
//    }
}

dependencies {
    api(project(":baselib"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)
    implementation(libs.arouter.api)
    kapt(libs.arouter.compiler)
}
fun Build_gradle.libPack() {
    project.android.libraryVariants.all {
        outputs.firstOrNull {
            it is LibraryVariantOutputImpl && it.name.contains("release")
        }?.also {
            packageLibraryProvider.get().destinationDirectory.asFileTree.filter {
                it.name.contains("release")
            }.firstOrNull()?.also {
                val aarPath = "..\\app\\libs"
                val date = SimpleDateFormat("yyyyMMddHHmm").format(Date())
                println("<------------${project.name}------------>")
                fileTree(aarPath).filter {
                    it.name.contains(project.name)
                }.forEach {
                    println("delete:${it.name}")
                    it.delete()
                }
                copy {
                    from(it)
                    into(aarPath)
                    rename {
                        "${project.name.lowercase(Locale.getDefault())}_${date}.aar"
                    }
                }
            }
            println("<-------------------------------------->")
        }
    }
}
