package io.mockative

import kotlin.reflect.KClass

/**
 * Creates a new mock instance of the specified type. The type must be declared in a property
 * annotated with the [Mock] annotation, in order for a mock for the type to be generated.
 *
 * This specific function is never actually called when Mockative is used correctly, because the
 * Mockative Kotlin Symbol Processor will generate a more specific overload of this function in the
 * same package with the file name `GeneratedMocks.kt`, accepting the specific type of the mock,
 * which overload resolution results in the compiler using instead.
 *
 * @param type the type to mock
 * @return an instance of the mocked type
 */
fun <T : Any> mock(type: KClass<T>): T = throw NoSuchMockError(type)
