import com.vanniktech.maven.publish.SonatypeHost
import java.util.*

plugins {
    signing
    id("com.vanniktech.maven.publish")
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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    if (signingKeyId != null && signingKey != null && signingPassword != null) {
        signAllPublications()
    }

    pom {
        name = "Mockative"
        description = "Mocking framework for Kotlin, Kotlin/Native and Kotlin Multiplatform"
        inceptionYear = "2021"
        url = "http://mockative.io"

        licenses {
            license {
                name = "MIT"
                url = "https://github.com/mockative/mockative/LICENSE"
                distribution = "https://github.com/mockative/mockative/LICENSE"
            }
        }

        developers {
            developer {
                id = "Nillerr"
                name = "Nicklas Jensen"
                email = "nicklas@mockative.io"
            }
        }

        scm {
            url = "https://github.com/mockative/mockative"
            connection = "scm:git:git://github.com/mockative/mockative.git"
            developerConnection = "scm:git:ssh://github.com/mockative/mockative.git"
        }
    }
}

val signingKeyId = props.getProperty("signing.keyId")
val signingKey = props.getProperty("signing.key")
val signingPassword = props.getProperty("signing.password")

if (signingKeyId != null && signingKey != null && signingPassword != null) {
    signing {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }
}
