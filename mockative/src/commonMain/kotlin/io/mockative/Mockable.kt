package io.mockative

import kotlin.reflect.KClass

/**
 * Enables mocking of the annotated type during test tasks.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Mockable(vararg val types: KClass<*>)
