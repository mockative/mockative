plugins {
    id("convention.multiplatform")
    id("convention.publication")
}

group = findProperty("project.group") as String
version = findProperty("project.version") as String

kotlin {
    sourceSets {

        jvmMain.configure {
            dependencies {
                implementation(kotlin("reflect"))
                implementation("org.objenesis:objenesis:3.5")
                implementation("org.javassist:javassist:3.29.2-GA")
            }
        }

        androidMain.configure {
            dependencies {
                implementation(kotlin("reflect"))
                implementation("org.objenesis:objenesis:3.5")
                implementation("org.javassist:javassist:3.29.2-GA")
            }
        }

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}
