rootProject.name = "PsychologyHelper"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":shared:core:domain")
include(":shared:core:ui")
include(":shared:core:data")
include(":shared:feature:auth")
include(":shared:feature:home")
include(":shared:feature:mood")
include(":shared:feature:journal")
include(":shared:feature:profile")
include(":shared:feature:chat")
