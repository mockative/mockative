package io.mockative.fake

import kotlin.reflect.KClass

inline fun <reified T> valueOf(): T {
    return valueOf(T::class)
}

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

internal expect fun <T> makeValueOf(type: KClass<*>): T
