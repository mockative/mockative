package io.mockative.generator

import java.io.InputStream

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
    readResourceText("io/mockative/generator/$name")

private object ClassLoaderRef