import org.gradle.api.JavaVersion

object Versions {
    const val compose = "1.3.1"


    object Java {
        const val jvmTarget = "17"
        val java = JavaVersion.VERSION_17
    }

    const val ktor = "2.2.3"

    object Kotlin {
        const val lang = "1.8.10"
        const val coroutines = "1.6.4"
        const val serialization = "1.4.0"
        const val datetime="0.4.0"
    }
    const val krypto = "3.0.1"
}