plugins {
    kotlin("multiplatform")
    id("convention.publication")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

kotlin {
    jvm()
    jvmToolchain(8)

    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(project(":mockative"))

                // KSP
                implementation("com.google.devtools.ksp:symbol-processing-api:1.9.22-1.0.16")

                // KotlinPoet
                implementation("com.squareup:kotlinpoet:1.14.2")
                implementation("com.squareup:kotlinpoet-ksp:1.14.2")
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
