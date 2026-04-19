pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolution {
    @Suppress("UNCHECKED_CAST")
    versionCatalogs {
        val libs by creating {
            // Kotlin
            version("kotlin", "2.1.20")
            // SQLDelight
            version("sqldelight", "2.0.2")
            // kotlinx
            version("coroutines", "1.10.2")
            version("serialization", "1.7.3")
            version("datetime", "0.6.2")
            // Android
            version("compose", "1.7.8")
            version("agp", "8.7.3")
            version("activityCompose", "1.9.3")
            // Testing
            version("turbine", "1.2.0")
        }
    }
}

rootProject.name = "open-imgs"

include(":shared")
include(":androidApp")
