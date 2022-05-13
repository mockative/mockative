plugins {
    kotlin("multiplatform")
    id("convention.publication")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

kotlin {
    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":mockative"))

                // KSP
                implementation("com.google.devtools.ksp:symbol-processing-api:1.6.20-1.0.5")

                // KotlinPoet
                implementation("com.squareup:kotlinpoet:1.10.2")
                implementation("com.squareup:kotlinpoet-ksp:1.10.2")
            }

            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }

        all {
            languageSettings {
                optIn("com.google.devtools.ksp.KspExperimental")
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}
