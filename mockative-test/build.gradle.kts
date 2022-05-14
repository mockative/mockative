plugins {
    kotlin("multiplatform")

    id("com.google.devtools.ksp")
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":mockative"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }

            kotlin.srcDir("generated/ksp/metadata/commonMain")
        }

        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
    }
}

afterEvaluate {
    configurations
        .filter { it.name.startsWith("ksp") }
        .forEach { configuration ->
            println("${configuration.name} <$configuration>")
        }
}

dependencies {
    add("kspJvmTest", project(":mockative-processor"))
}

ksp {
    arg("mockative.logging", "debug")
    arg("mockative.stubsUnitByDefault", "true")
}