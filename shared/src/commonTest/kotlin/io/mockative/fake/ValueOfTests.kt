package io.mockative.fake

import io.mockative.AbstractParameter
import io.mockative.Repository
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ValueOfTests {
    @Test
    fun valueOfReturnsStandardTypes() {
        assertEquals(false, valueOf(Boolean::class))
        assertEquals(0.toByte(), valueOf(Byte::class))
        assertEquals(0.toShort(), valueOf(Short::class))
        assertEquals(0.toChar(), valueOf(Char::class))
        assertEquals(0, valueOf(Int::class))
        assertEquals(0L, valueOf(Long::class))
        assertEquals(0f, valueOf(Float::class))
        assertEquals(0.0, valueOf(Double::class))

        assertContentEquals(BooleanArray(0), valueOf(BooleanArray::class))
        assertContentEquals(ByteArray(0), valueOf(ByteArray::class))
        assertContentEquals(ShortArray(0), valueOf(ShortArray::class))
        assertContentEquals(CharArray(0), valueOf(CharArray::class))
        assertContentEquals(IntArray(0), valueOf(IntArray::class))
        assertContentEquals(LongArray(0), valueOf(LongArray::class))
        assertContentEquals(FloatArray(0), valueOf(FloatArray::class))
        assertContentEquals(DoubleArray(0), valueOf(DoubleArray::class))

        assertEquals(emptyList<Nothing>(), valueOf(List::class))
        assertEquals(emptyMap<Nothing, Nothing>(), valueOf(Map::class))
        assertEquals(emptySet<Nothing>(), valueOf(Set::class))

        assertEquals(arrayListOf<Nothing>(), valueOf(ArrayList::class))
        assertEquals(hashMapOf<Nothing, Nothing>(), valueOf(HashMap::class))
        assertEquals(hashSetOf<Nothing>(), valueOf(HashSet::class))

        assertEquals("", valueOf(String::class))
    }

    @Test
    fun makeValueOfReturnsOtherStuff() {
        val functionType: () -> Unit = {}
        assertNotNull(valueOf(functionType::class))

        assertNotNull(valueOf(Repository::class))

        assertNotNull(valueOf(AbstractParameter::class))
    }
}
