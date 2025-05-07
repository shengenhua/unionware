pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/nexus/content/repositories/jcenter")
        maven("https://maven.ghostscript.com/")
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/nexus/content/repositories/jcenter")
        maven("https://maven.ghostscript.com/")
        maven("https://jitpack.io")
        google()
        mavenCentral()
        maven("https://maven.ghostscript.com/")
    }
}

rootProject.name = "轻优联MES"
include(":app")
include(":baselib")
//include(":basicui")
include(":MES")
include(":ONCE")
include(":WMS")
include(":basePrinter")
include(":QUERY")
include(":QUERY")
include(":EMES")
include(":MES_Process")
//include(":unionwareLib")
