package io.github

import io.mockative.Mockable

// Enum class
enum class Color { RED, GREEN, BLUE }

// Object type
object Singleton {
    val value = "singleton"
}

// Inline/value class
@kotlin.jvm.JvmInline
value class UserId(val id: String)

// Non-@Mockable interface used as a parameter type
interface NonMockableInterface {
    fun doSomething(): String
}

// Abstract class with constructor parameters
@Mockable
abstract class AbstractWithConstructor(val name: String) {
    abstract fun compute(): Int
}

// Deeply nested sealed hierarchy
sealed interface DeepSealedRoot {
    sealed interface Level1A : DeepSealedRoot {
        sealed interface Level2A : Level1A {
            data class Leaf(val value: String) : Level2A
        }
    }
    sealed interface Level1B : DeepSealedRoot {
        data object SingletonLeaf : Level1B
    }
}

// Sealed class (not interface)
sealed class SealedClass {
    data class DataVariant(val x: Int) : SealedClass()
    object ObjectVariant : SealedClass()
}

// Interface that uses all these gap types as parameters/return types,
// so the KSP processor discovers them for makeValueOf generation.
@Mockable
interface ValueOfGapService {
    // Enum parameter
    fun acceptEnum(color: Color)
    fun returnEnum(): Color

    // Object parameter
    fun acceptObject(singleton: Singleton)

    // Inline/value class parameter
    fun acceptInlineClass(userId: UserId)
    fun returnInlineClass(): UserId

    // Non-@Mockable interface parameter
    fun acceptNonMockableInterface(value: NonMockableInterface)

    // Abstract class with constructor params
    fun acceptAbstractWithConstructor(value: AbstractWithConstructor)

    // Deep sealed hierarchy
    fun acceptDeepSealed(value: DeepSealedRoot)

    // Sealed class (not interface)
    fun acceptSealedClass(value: SealedClass)

    // Function types
    fun acceptFunction0(block: () -> Unit)
    fun acceptFunction1(block: (String) -> Int)
    fun acceptSuspendFunction(block: suspend () -> String)

    // Nullable custom types
    fun acceptNullableEnum(color: Color?)
    fun acceptNullableInterface(value: NonMockableInterface?)
}

// Interface with function-level type parameters (like CoroutineContext.get)
interface TypeParameterizedMethods {
    fun <T> genericFunction(value: T): T
    fun <K, V> multiGenericFunction(key: K, value: V): Map<K, V>
}

// Service that takes TypeParameterizedMethods as a parameter,
// forcing the processor to generate a Fake__ class for it on Native.
@Mockable
interface GenericMethodService {
    fun acceptGenericMethods(handler: TypeParameterizedMethods)
}
