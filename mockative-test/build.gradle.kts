plugins {
    kotlin("multiplatform")

    id("com.google.devtools.ksp")
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("src/main/kotlin")
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":mockative"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }

            kotlin.srcDir("src/test/kotlin")
            resources.srcDir("src/test/resources")
        }
    }
}

dependencies {
    ksp(project(":mockative-processor"))
}