plugins {
    id("convention.multiplatform")
    id("com.google.devtools.ksp")
}

version = findProperty("project.version") as String

inline fun <reified T> getProperty(key: String): T {
    return findProperty(key) as T? ?: throw NoSuchElementException("A project property with key '$key' was not found.")
}

val sonatypeRepository: String = getProperty("sonatype.repository")
val sonatypeUsername: String = getProperty("sonatype.username")
val sonatypePassword: String = getProperty("sonatype.password")

repositories {
    maven {
        url = uri("https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$sonatypeRepository")

        credentials {
            setUsername(sonatypeUsername)
            setPassword(sonatypePassword)
        }
    }
}

kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation("io.mockative:mockative:$version")
            }
        }
    }
}

dependencies {
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, "io.mockative:mockative-processor:$version")
        }
}
