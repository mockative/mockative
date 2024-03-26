plugins {
    kotlin("jvm")
    application
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("io.mockative.generator.MainKt")
}
