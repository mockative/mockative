package io.github

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.every
import io.mockative.spy
import kotlin.test.Test
import kotlin.test.assertEquals

class SpyTests {
    val realSpy = SpyClass("realSpy")

    @Mock(isSpy = true)
    val spy: SpyClass = spy(classOf<SpyClass>(), realSpy)

    @Test
    fun noMockingspy() {
        val spyOutput = spy.test()
        val realOutput = realSpy.test()

        assertEquals(spyOutput, realOutput)
    }

    @Test
    fun mockingSpy() {
        every {
            spy.test()
        }.returns("mocked")

        val spyOutput = spy.test()

        assertEquals(spyOutput, "mocked")
    }
}