plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hint.android)
    id("maven-publish")
    kotlin("kapt")
}

kapt {
    correctErrorTypes = true
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
        includeCompileClasspath = true
    }
}
android {
    namespace = "unionware.base"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    sourceSets {
        this.stream().filter { it.name == "main" }.forEach {
            it.res.srcDirs(
                "src/main/res",
                "src/main/res/basicui",
                "src/main/res/base",
            )
        }
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.junit)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)



    api(libs.utilcodex)
    api(libs.lucksiege.pictureselector)
    api(libs.lucksiege.compress)
    api(libs.lucksiege.ucrop)
    api(libs.bumptech.glide)
//    api(libs.bumptech.compiler)
    api(libs.eventbus)
    api(libs.xpopup)
    api(libs.refresh.layout.kernel)
    api(libs.refresh.header.classics)
    api(libs.refresh.header.radar)
    api(libs.refresh.header.falsify)
    api(libs.refresh.header.material)
    api(libs.refresh.footer.classics)
    api(libs.refresh.footer.ball)
    api(libs.retrofit2.retrofit)
    api(libs.retrofit2.converter.moshi)
    api(libs.retrofit2.converter.gson)
    api(libs.retrofit2.squareup.adapter.rxjava2)
    api(libs.rxjava2.rxandroid)
    api(libs.mmkv)
    api(libs.github.franmontiel.persistentcookiejar11)
    api(libs.okhttp3.logging.interceptor2)
    api(libs.okhttp3.mockwebserver)
    api(libs.geyifeng.immersionbar)
    api(libs.geyifeng.immersionbar.ktx)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.livedata.ktx)
    api(libs.cymchad.adapterhelper)
    api(libs.cymchad.adapterhelper4)
    api(libs.gson)
    api(libs.zxing.core)
    api(libs.zxing.android.embedded)
    api(libs.zxing.android.embedded)
    api(libs.org.apache.commons.commons.collections4)
    api(libs.org.apache.commons.commons.lang3)



    api(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)
//    api(libs.dagger.hilt.android)
//    kapt(libs.dagger.hilt.compiler)

    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    api(libs.arouter.api)
    kapt(libs.arouter.compiler)
}

tasks.withType<GenerateMavenPom>().all {
    doLast {
        val file =
            File("$buildDir/publications/release/pom-default.xml")//maven值自己看看对应build/publications/下是什么
        var text = file.readText()
        val regex =
            "(?s)(<dependencyManagement>.+?<dependencies>)(.+?)(</dependencies>.+?</dependencyManagement>)".toRegex()
        val matcher = regex.find(text)
        if (matcher != null) {
            text = regex.replaceFirst(text, "")
            val firstDeps = matcher.groups[2]!!.value
            text = regex.replaceFirst(text, "$1$2$firstDeps$3")
        }
        file.writeText(text)
    }
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "unionware.base"
                artifactId = "baselib"
                version = "1.0.1"
                from(components["release"])
                versionMapping {
                    usage("java-api") {
                        fromResolutionResult()
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
                pom {
                    withXml {
                        asNode().appendNode("properties").apply {
                            appendNode("AROUTER_MODULE_NAME", project.name)
                            appendNode("kapt.includeCompileClasspath", "true")
                        }
                    }
                }
            }
        }
    }
}