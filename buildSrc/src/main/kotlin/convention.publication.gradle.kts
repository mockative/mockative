import java.util.*

plugins {
    `maven-publish`
    signing
}

val props = Properties().apply {
    // Load `local.properties`
    loadFile(project.rootProject.file("local.properties"), required = false)

    // Load environment variables
    loadEnv("gpr.user", "GPR_USER")
    loadEnv("gpr.key", "GPR_KEY")

    loadEnv("signing.keyId", "SIGNING_KEY_ID")
    loadEnv("signing.key", "SIGNING_KEY")
    loadEnv("signing.password", "SIGNING_PASSWORD")

    loadEnv("ossrh.username", "OSSRH_USERNAME")
    loadEnv("ossrh.password", "OSSRH_PASSWORD")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/mockative/mockative")
            credentials {
                username = props.getProperty("gpr.user")
                password = props.getProperty("gpr.key")
            }
        }

        maven {
            name = "Sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = props.getProperty("ossrh.username")
                password = props.getProperty("ossrh.password")
            }
        }

        maven {
            name = "SonatypeSnapshot"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = props.getProperty("ossrh.username")
                password = props.getProperty("ossrh.password")
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
