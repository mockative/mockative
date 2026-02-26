buildscript {
    repositories {
        // TODO: Remove this before merge
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
        classpath("com.android.tools.build:gradle:8.12.3")
    }
}

allprojects {
    repositories {
        // TODO: Remove this before merge
        mavenLocal()
        google()
        mavenCentral()
    }
}
