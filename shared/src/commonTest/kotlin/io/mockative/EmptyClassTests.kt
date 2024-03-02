package io.mockative

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmptyClassTests {
    @Mock
    val mock = mock(classOf<EmptyClass>())

    val notMock = object : EmptyClass() {}

    @Test
    fun isMockEmptyClass() {
        assertTrue(isMock(mock))
    }

    @Test
    fun isNotMockEmptyClass() {
        assertFalse(isMock(notMock))
    }
}
