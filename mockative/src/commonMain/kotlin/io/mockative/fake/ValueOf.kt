package io.mockative.fake

import kotlin.reflect.KClass

/**
 * Creates a new value of the specified type [T] according to these rules:
 *
 * - [Boolean] return `false`
 * - [Number] and [Char] types return `0`
 * - [Array], [List], [Map], [Set], [ArrayList], [HashMap] and [HashSet] return an instance with 0 elements
 * - [String] and [CharSequence] return an empty string
 *
 * The return value of any other type than those mentioned in the list above depend on the target platform:
 *
 * - JVM (including Android): A combination of java's `Proxy`, [Objenesis](https://objenesis.org/) and
 * [Javassist](https://www.javassist.org/) is used to provide a value.
 * - Other platforms: This function simply returns [Unit], as these platforms conveniently don't do type checks
 * until a member on the instance is used. For this reason, this function is only intended to be used to provide
 * default value that will not actually be used by the executing program, such as when providing a value during the
 * recording that happens when stubbing a member on a mock.
 *
 * @param T The type to create a value of.
 *
 * @return The created value of the specified type.
 */
inline fun <reified T> valueOf(): T {
    return valueOf(T::class)
}

/**
 * Creates a new value of the specified [type] according to these rules:
 *
 * - [Boolean] return `false`
 * - [Number] and [Char] types return `0`
 * - [Array], [List], [Map], [Set], [ArrayList], [HashMap] and [HashSet] return an instance with 0 elements
 * - [String] and [CharSequence] return an empty string
 *
 * The return value of any other type than those mentioned in the list above depend on the target platform:
 *
 * - JVM (including Android): A combination of java's `Proxy`, [Objenesis](https://objenesis.org/) and
 * [Javassist](https://www.javassist.org/) is used to provide a value.
 * - Other platforms: This function simply returns [Unit], as these platforms conveniently don't do type checks
 * until a member on the instance is used. For this reason, this function is only intended to be used to provide
 * default value that will not actually be used by the executing program, such as when providing a value during the
 * recording that happens when stubbing a member on a mock.
 *
 * @param type The type to create a value of.
 *
 * @return The created value of the specified type.
 */
@Suppress("UNCHECKED_CAST")
fun <T> valueOf(type: KClass<*>): T {
    return when (type) {
        Boolean::class -> false as T
        Byte::class -> 0.toByte() as T
        Short::class -> 0.toShort() as T
        Char::class -> 0.toChar() as T
        Int::class -> 0 as T
        Long::class -> 0L as T
        Float::class -> 0f as T
        Double::class -> 0.0 as T

        BooleanArray::class -> BooleanArray(0) as T
        ByteArray::class -> ByteArray(0) as T
        ShortArray::class -> ShortArray(0) as T
        CharArray::class -> CharArray(0) as T
        IntArray::class -> IntArray(0) as T
        LongArray::class -> LongArray(0) as T
        FloatArray::class -> FloatArray(0) as T
        DoubleArray::class -> DoubleArray(0) as T

        Array::class -> emptyArray<Any>() as T
        List::class -> emptyList<Nothing>() as T
        Map::class -> emptyMap<Nothing, Nothing>() as T
        Set::class -> emptySet<Nothing>() as T

        ArrayList::class -> arrayListOf<Nothing>() as T
        HashMap::class -> hashMapOf<Nothing, Nothing>() as T
        HashSet::class -> hashSetOf<Nothing>() as T

        String::class -> "" as T
        CharSequence::class -> "" as T

        else -> makeValueOf(type)
    }
}
