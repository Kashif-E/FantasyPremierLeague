plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("io.realm.kotlin") version Versions.realm
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

// CocoaPods requires the podspec to have a version.
version = "1.0"

android {
    compileSdk = AndroidSdk.compile
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

kotlin {
    val iosTarget: (String, org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.() -> Unit) -> org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64 // available to KT 1.5.30
        else -> ::iosX64
    }
    iosTarget("iOS") {}


    android()
    jvm()

    cocoapods {
        summary = "Fantasy Football Premier League"
        homepage = "Link to a Kotlin/Native module homepage"
        frameworkName = "FantasyPremierLeagueKit"
    }

    sourceSets {

        sourceSets["commonMain"].dependencies {
            with(Deps.Ktor) {
                implementation(clientCore)
                implementation(clientJson)
                implementation(clientLogging)
                implementation(clientSerialization)
                implementation(contentNegotiation)
                implementation(json)
            }

            with(Deps.Kotlinx) {
                implementation(coroutinesCore)
                implementation(serializationCore)
                api(dateTime)
            }

            // Realm
            implementation(Deps.realm)

            // koin
            api(Koin.core)
            api(Koin.test)

            // kermit
            api(Deps.kermit)
        }

        val androidMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientAndroid)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientJava)
                implementation(Deps.Ktor.slf4j)

                implementation("org.nield:kotlin-statistics:1.2.1")
                implementation("org.ojalgo:okalgo:0.0.2")
                implementation("org.jetbrains.kotlinx:multik-api:0.1.0")
                implementation("org.jetbrains.kotlinx:multik-jvm:0.1.0")
            }
        }


        val iOSMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientIos)
            }
        }
        val iOSTest by getting {
        }
    }
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xuse-experimental=kotlin.time.ExperimentalTime")
    }
}


multiplatformSwiftPackage {
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }
}

