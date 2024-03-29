import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish.base")
    kotlin("plugin.serialization").version(Versions.Kotlin.lang)
}

kotlin {
    ios()
    iosArm64()
    iosX64()
    macosX64()
    macosArm64()
    android()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.Java.jvmTarget
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}")
                implementation("io.ktor:ktor-server-websockets:${Versions.ktor}")
                implementation("io.ktor:ktor-client-core:${Versions.ktor}")
                implementation("io.ktor:ktor-client-cio:${Versions.ktor}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Kotlin.serialization}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Versions.Kotlin.datetime}")
                implementation("com.soywiz.korlibs.krypto:krypto:${Versions.krypto}")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }


        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:${Versions.ktor}")
            }
        }
        val darwinMain by creating {
            dependsOn(commonMain)
            dependencies{
                implementation("io.ktor:ktor-client-darwin:${Versions.ktor}")
            }
        }
        val androidMain by getting {
            dependsOn(jvmMain)
        }

        val iosMain by getting{
            dependsOn(darwinMain)
        }
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        val macosX64Main by getting {
            dependsOn(darwinMain)
        }

        val macosArm64Main by getting {
            dependsOn(darwinMain)
        }

    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = Versions.Java.java
        targetCompatibility = Versions.Java.java
    }
}

@Suppress("UnstableApiUsage")
configure<MavenPublishBaseExtension> {
    configure(KotlinMultiplatform())
}