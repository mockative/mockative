buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
        classpath("com.android.tools.build:gradle:8.5.2")
    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
