package io.mockative

import kotlin.test.Test
import kotlin.test.assertIs

class EmptyInterfaceTests {
    @Mock
    val instance = mock(classOf<EmptyInterface>())

    @Test
    fun isMockEmptyInterface() {
        assertIs<Mockable>(instance)
    }
}
