buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0-RC3")
        classpath("com.android.tools.build:gradle:8.4.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
