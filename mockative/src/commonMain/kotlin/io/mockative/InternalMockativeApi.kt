package io.mockative

/**
 * Marks declarations that are internal to Mockative and should not be used by consumers.
 *
 * These APIs may change or be removed without notice and offer no stability guarantees.
 */
@RequiresOptIn(
    message = "This is an internal Mockative API that should not be used by consumers. " +
        "It may change or be removed without notice.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class InternalMockativeApi
