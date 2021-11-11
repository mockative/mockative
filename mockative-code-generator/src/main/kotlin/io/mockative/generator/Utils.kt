package io.mockative.generator

import java.io.InputStream

internal class NoSuchResourceError(name: String) :
    Error("A resource with the name `$name` could not be found in the given class loader.")

internal fun InputStream.readText(): String =
    reader().use { it.readText() }

internal fun ClassLoader.readResourceText(name: String): String =
    (getResourceAsStream(name) ?: throw NoSuchResourceError(name)).readText()

internal fun Class<*>.readResourceText(name: String): String =
    classLoader.readResourceText(name)

internal fun readResourceText(name: String): String =
    ClassLoaderRef.javaClass.readResourceText(name)

internal fun readResourceSource(name: String): String =
    readResourceText("io/mockative/generator/$name")

private object ClassLoaderRef