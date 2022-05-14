package io.mockative

import java.io.File
import java.io.InputStream
import kotlin.test.assertEquals

class NoSuchResourceError(name: String) :
    Error("A resource with the name `$name` could not be found in the given class loader.")

fun InputStream.readText(): String =
    reader().use { it.readText() }

fun ClassLoader.readResourceText(name: String): String =
    (getResourceAsStream(name) ?: throw NoSuchResourceError(name)).readText()

fun Class<*>.readResourceText(name: String): String =
    classLoader.readResourceText(name)

fun readResourceText(name: String): String =
    ClassLoaderRef.javaClass.readResourceText(name)

fun readResourceSource(name: String): String =
    readResourceText("io/mockative/$name")

fun readGeneratedText(name: String): String =
    File("build/generated/ksp/jvmTest/kotlin", name)
        .absoluteFile
        .readText()

fun readGeneratedSource(name: String): String =
    readGeneratedText("io/mockative/$name")

fun assertGenerated(name: String) {
    val resource = readResourceSource(name)
    val generated = readGeneratedSource(name)
    assertEquals(resource, generated)
}

private object ClassLoaderRef