import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Single source of truth for versionCode/versionName: the topmost "<code> <title>" / "<description>"
// entry in VERSIONS.md (two lines, blank line between entries). Bump it there (see the
// release-version skill), not here.
data class AppVersion(val code: Int, val name: String)

fun latestAppVersion(): AppVersion {
    val headerRegex = Regex("^(\\d+)\\s+(\\S.*)$")
    val entryHeaders = rootProject.file("VERSIONS.md").readText()
        .split(Regex("\n\\s*\n"))
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.lineSequence().first() }
        .filter { headerRegex.matches(it) }
    val top = entryHeaders.firstOrNull()
        ?: error("VERSIONS.md has no '<code> <title>' entry")
    val match = headerRegex.find(top)!!
    val code = match.groupValues[1].toInt()
    val name = match.groupValues[2].trim()
    check(code == entryHeaders.size) {
        "VERSIONS.md: top entry's code ($code) must equal the number of entries in the file " +
            "(${entryHeaders.size}) — bump it when adding a new version."
    }
    return AppVersion(code, name)
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.androidx.splashscreen)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.voyager.navigator)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.kotlinx.coroutines.core)
            // All modules — explicit deps needed so PsychologyApplication can access Koin modules
            implementation(project(":shared:core:domain"))
            implementation(project(":shared:core:data"))
            implementation(project(":shared:core:ui"))
            implementation(project(":shared:feature:auth"))
            implementation(project(":shared:feature:home"))
            implementation(project(":shared:feature:mood"))
            implementation(project(":shared:feature:journal"))
            implementation(project(":shared:feature:profile"))
            implementation(project(":shared:feature:chat"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.morozione.psychologyhelper"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.morozione.psychologyhelper"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        val appVersion = latestAppVersion()
        versionCode = appVersion.code
        versionName = appVersion.name
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}
