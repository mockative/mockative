package io.mockative

import kotlin.reflect.KClass

/**
 * Creates a new mock instance of the specified type. The type must be declared in a property
 * annotated with the [Mock] annotation, in order for a mock for the type to be generated.
 *
 * This specific function is never actually called when Mockative is used correctly, because the
 * Mockative Kotlin Symbol Processor will generate a more specific overload of this function in the
 * same package, accepting the specific type of the mock, which overload resolution results in the
 * compiler using instead.
 *
 * @param type the type to mock
 * @return an instance of the mocked type
 * @see of
 */
fun <T : Any> mock(type: KClass<T>): T = throw NoSuchMockException(type)

/**
 * Checks if the specified [value] is a Mockative mock instance.
 *
 * @param value The value to check
 * @return `true` if the [value] is a Mockative mock; `false` otherwise.
 */
fun <T : Any> isMock(value: T): Boolean {
    val name = value.getClassName()
    return name.endsWith("Mock")
}

fun <T : Any> spy(type: KClass<T>, @Suppress("UNUSED_PARAMETER") instance: T): T = throw NoSuchMockException(type)

fun <T : Any> spyOn(instance: T): T = throw NoSuchMockException(instance::class)
