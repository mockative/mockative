plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    implementation("com.android.tools.build:gradle:8.0.2")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.30.0")
}
