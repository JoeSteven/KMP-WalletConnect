import org.gradle.api.JavaVersion

object Versions {
    const val compose = "1.2.0-alpha01-dev755"


    object Java {
        const val jvmTarget = "17"
        val java = JavaVersion.VERSION_17
    }

    const val ktor = "2.1.0"

    object Kotlin {
        const val lang = "1.7.10"
        const val coroutines = "1.6.4"
        const val serialization = "1.4.0"
    }
}