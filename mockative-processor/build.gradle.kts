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

                implementation(kotlin("reflect"))
                implementation(kotlin("stdlib"))
                implementation(kotlin("metadata-jvm"))

                // KSP
                implementation("com.google.devtools.ksp:symbol-processing-api:2.3.4")

                // KotlinPoet
                implementation("com.squareup:kotlinpoet:2.2.0")
                implementation("com.squareup:kotlinpoet-ksp:2.2.0")
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
