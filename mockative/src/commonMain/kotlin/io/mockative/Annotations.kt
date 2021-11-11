package io.mockative

/**
 * Enables mocking of the type specified by an annotated property.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Mock
