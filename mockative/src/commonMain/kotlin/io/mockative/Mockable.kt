package io.mockative

/**
 * Enables mocking of the annotated type during test tasks.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Mockable
