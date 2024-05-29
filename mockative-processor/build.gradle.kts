plugins {
    kotlin("multiplatform")
    id("convention.publication")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

kotlin {
    jvm()

    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(project(":mockative"))

                // KSP
                implementation("com.google.devtools.ksp:symbol-processing-api:2.0.0-1.0.21")

                // KotlinPoet
                implementation("com.squareup:kotlinpoet:1.16.0")
                implementation("com.squareup:kotlinpoet-ksp:1.16.0")
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
