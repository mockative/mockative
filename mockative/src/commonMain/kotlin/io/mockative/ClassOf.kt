package io.mockative

import kotlin.reflect.KClass

/**
 * Returns an instance of [KClass] while retaining the generic type information when passed to a
 * function accepting an instance of [KClass], allowing that function to use the type arguments.
 */
inline fun <reified T : Any> classOf(): KClass<T> = T::class
