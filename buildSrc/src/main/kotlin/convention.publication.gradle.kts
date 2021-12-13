import java.util.*

plugins {
    `maven-publish`
    signing
}

val props = Properties().apply {
    // Load `gradle.properties`, environment variables and command-line arguments
    project.properties.forEach { (key, value) ->
        if (value != null) {
            this[key] = value
        }
    }

    // Load `local.properties`
    loadFile(project.rootProject.file("local.properties"), required = false)

    // Load environment variables
    loadEnv("signing.keyId", "SIGNING_KEY_ID")
    loadEnv("signing.key", "SIGNING_KEY")
    loadEnv("signing.password", "SIGNING_PASSWORD")

    loadEnv("sonatype.username", "SONATYPE_USERNAME")
    loadEnv("sonatype.password", "SONATYPE_PASSWORD")
    loadEnv("sonatype.repository", "SONATYPE_REPOSITORY")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            name = "Sonatype"
            url = props.getProperty("sonatype.repository")
                ?.let { uri("https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$it") }
                ?: uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")

            credentials {
                username = props.getProperty("sonatype.username")
                password = props.getProperty("sonatype.password")
            }
        }

        maven {
            name = "SonatypeSnapshot"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = props.getProperty("sonatype.username")
                password = props.getProperty("sonatype.password")
            }
        }
    }

    publications {
        withType<MavenPublication> {
            artifact(javadocJar.get())

            pom {
                name.set("Mockative")
                description.set("Mocking framework for Kotlin, Kotlin/Native and Kotlin Multiplatform")
                url.set("http://mockative.io")

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/mockative/mockative/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("Nillerr")
                        name.set("Nicklas Jensen")
                        email.set("nicklas@mockative.io")
                    }
                }

                scm {
                    url.set("https://github.com/mockative/mockative")
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        props.getProperty("signing.keyId"),
        props.getProperty("signing.key"),
        props.getProperty("signing.password"),
    )

    sign(publishing.publications)
}
