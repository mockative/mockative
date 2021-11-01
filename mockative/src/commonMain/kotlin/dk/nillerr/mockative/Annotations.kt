package dk.nillerr.mockative

import kotlin.reflect.KClass

/**
 * Enables mocking of types specified by an annotated property.
 *
 * Use of mocks through annotated properties can cause `InvalidMutabilityException`s to be thrown
 * in Kotlin/Native when running tests in a separate thread than the test is set up in. To use
 * mocks through annotation properties, use an implementation of `runBlockingTest` that simply
 * wraps `runBlocking` on iOS.
 */
@RequiresOptIn
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class PropertyMocks

/**
 * Enables mocking of the type specified by an annotated property.
 *
 * This annotation is marked as opt-in because use of property mocks can cause
 * `InvalidMutabilityException`s to be thrown in Kotlin/Native when running tests in a separate
 * thread than the test is set up in.
 *
 * @see PropertyMocks
 */
@PropertyMocks
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Mock

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Mocks(val value: KClass<*>)
