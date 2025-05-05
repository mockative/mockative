package io.mockative

import kotlin.reflect.KClass

/**
 * Returns an instance of [KClass] while retaining the generic type information when passed to a
 * function accepting an instance of [KClass], allowing that function to use the type arguments.
 *
 * This function serves as a convenient way to avoid having to add a type cast when working with generics.
 */
inline fun <reified T : Any> of(): KClass<T> = T::class

/**
 * Returns an instance of [KClass] while retaining the generic type information when passed to a
 * function accepting an instance of [KClass], allowing that function to use the type arguments.
 *
 * This function serves as a convenient way to avoid having to add a type cast when working with generics.
 */
@Deprecated(message = "Replaced by `of`", replaceWith = ReplaceWith("of<T>()"))
inline fun <reified T : Any> classOf(): KClass<T> = T::class
