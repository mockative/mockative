package io.github.fake

import io.github.AbstractParameter
import io.github.Repository
import io.mockative.fake.valueOf
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ValueOfTests {
    @Test
    fun valueOfReturnsStandardTypes() {
        assertEquals(false, valueOf<Boolean>())
        assertEquals(0.toByte(), valueOf<Byte>())
        assertEquals(0.toShort(), valueOf<Short>())
        assertEquals(0.toChar(), valueOf<Char>())
        assertEquals(0, valueOf<Int>())
        assertEquals(0L, valueOf<Long>())
        assertEquals(0f, valueOf<Float>())
        assertEquals(0.0, valueOf<Double>())

        assertContentEquals(BooleanArray(0), valueOf<BooleanArray>())
        assertContentEquals(ByteArray(0), valueOf<ByteArray>())
        assertContentEquals(ShortArray(0), valueOf<ShortArray>())
        assertContentEquals(CharArray(0), valueOf<CharArray>())
        assertContentEquals(IntArray(0), valueOf<IntArray>())
        assertContentEquals(LongArray(0), valueOf<LongArray>())
        assertContentEquals(FloatArray(0), valueOf<FloatArray>())
        assertContentEquals(DoubleArray(0), valueOf<DoubleArray>())

        assertEquals(emptyList(), valueOf<List<Nothing>>())
        assertEquals(emptyMap(), valueOf<Map<Nothing, Nothing>>())
        assertEquals(emptySet(), valueOf<Set<Nothing>>())

        assertEquals(arrayListOf(), valueOf<ArrayList<Nothing>>())
        assertEquals(hashMapOf(), valueOf<HashMap<Nothing, Nothing>>())
        assertEquals(hashSetOf(), valueOf<HashSet<Nothing>>())

        assertEquals("", valueOf<String>())
        assertEquals("", valueOf<CharSequence>())
    }

    @Test
    fun makeValueOfReturnsOtherStuff() {
        assertNotNull(valueOf<() -> Unit>())
        assertNotNull(valueOf<Repository>())
        assertNotNull(valueOf<AbstractParameter>())
    }
}
