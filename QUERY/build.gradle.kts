import com.android.build.gradle.internal.api.LibraryVariantOutputImpl
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hint.android)
    kotlin("kapt")
}
kapt {
    correctErrorTypes = true
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}

android {
    namespace = "com.unionware.query"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
//    libPack()
}

dependencies {

    implementation(project(":baselib"))
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