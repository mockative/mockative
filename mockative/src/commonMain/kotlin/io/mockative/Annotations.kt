package io.mockative

/**
 * Enables mocking of the type specified by an annotated property.
 *
 * Using this annotation only enables code generation for mocking the specified type, and as such
 * must be used in combination with [mock] and [classOf] to instantiate an instance of the mock.
 *
 * @see mock
 * @see classOf
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Mock(val isSpy: Boolean = false)
