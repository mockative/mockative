package io.mockative

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class EmptyInterfaceTests {
    @Mock
    val mock = mock(classOf<EmptyInterface>())

    val notMock = object : EmptyInterface() {}

    @Test
    fun isMockEmptyInterface() {
        assertTrue(isMock(mock))
    }

    @Test
    fun isNotMockEmptyInterface() {
        assertFalse(isMock(notMock))
    }
}
