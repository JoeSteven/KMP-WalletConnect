import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    kotlin("android").apply(false)
    id("com.vanniktech.maven.publish") version "0.25.1" apply false
}

group = "io.github.joesteven"
version = "1.1.3"

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = Versions.Java.jvmTarget
            // allWarningsAsErrors = true
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers",
                "-Xskip-prerelease-check",
            )
        }
    }

    plugins.withId("com.vanniktech.maven.publish.base") {
        @Suppress("UnstableApiUsage")
        configure<MavenPublishBaseExtension> {
            publishToMavenCentral(SonatypeHost.S01)
            signAllPublications()
            pom {
                group = "io.github.joesteven"
                version = "1.1.3"
                name.set("kmp-walletconnect")
                description.set("Video player for Kotlin multiplatform")
                url.set("https://github.com/JoeSteven/KMP-VideoPlayer/")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("Mimao")
                        name.set("Mimao")
                        email.set("qiaoxiaoxi621@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/JoeSteven/KMP-WalletConnect/")
                    connection.set("scm:git:git://github.com/JoeSteven/KMP-WalletConnect.git")
                    developerConnection.set("scm:git:git://github.com/JoeSteven/KMP-WalletConnect.git")
                }
            }
        }
    }
}