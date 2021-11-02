plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":mockative"))
                implementation("com.google.devtools.ksp:symbol-processing-api:1.5.31-1.0.0")
            }

            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        all {
            languageSettings {
                optIn("com.google.devtools.ksp.KspExperimental")
            }
        }
    }
}
