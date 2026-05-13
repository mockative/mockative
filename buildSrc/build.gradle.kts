plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
    implementation("com.android.tools.build:gradle:9.2.0")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.30.0")
}
