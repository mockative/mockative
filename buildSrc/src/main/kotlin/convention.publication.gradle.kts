plugins {
    `maven-publish`
    signing
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/mockative/mockative")
            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
                password = findProperty("gpr.key") as String? ?: System.getenv("GPR_KEY")
            }
        }
    }

    publications {
        withType<MavenPublication> {
            pom {
                name.set("Mockative")
                description.set("Mocking framework for Kotlin/Native and Kotlin Multiplatform")
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

//signing {
//    sign(publishing.publications)
//}
