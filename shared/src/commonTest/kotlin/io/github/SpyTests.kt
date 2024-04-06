package io.github

import io.mockative.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SpyTests {
    val realSpy = SpyClass("realSpy")
    @Mock(isSpy = true)
    val spy: SpyClass = spy(classOf<SpyClass>(), realSpy)

    val realList = mutableListOf("real")
    @Mock(isSpy = true)
    val spyList: MutableList<String> = spy(classOf<MutableList<String>>(), realList)


    @Test
    fun noMockingspy() {
        // Given / When
        val spyOutput = spy.greet()
        val realOutput = realSpy.greet()

        // Then
        assertEquals(spyOutput, realOutput)
    }

    @Test
    fun mockingSpy() {
        // Given
        every {
            spy.greet()
        }.returns("mocked")

        // When
        val spyOutput = spy.greet()

        // Then
        assertEquals(spyOutput, "mocked")
    }

    @Test
    fun mockingList() {
        // Given
        every {
            spyList[0]
        }.returns("mocked")

        // When
        val listOutput = spyList[0]

        // Then
        assertEquals(listOutput, "mocked")
    }

    @Test
    fun noMockingList() {
        // Given
        spyList.addAll(listOf("I", "am"))

        // When
        val listOutput = spyList.reduce { acc, s -> "$acc $s" }

        // Then
        assertEquals("real I am", listOutput)
    }

    @Test
    fun mockingListWithReal() {
        // Given
        every {
            spyList[0]
        }.returns("mocked")
        every {
            spyList[2]
        }.returns("I am not am")

        // When
        spyList.addAll(listOf("hello there", "am"))
        val output = "${spyList[0]} ${spyList[1]} ${spyList[2]}"

        // Then
        assertEquals("mocked hello there I am not am", output)
    }

    @Test
    fun givenFiniteAmountOfAnswers_whenExhaustingAllStubs_thenExpectFallbackToRealImplementation() {
        // Given
        every {
            spyList.size
        }.returnsMany(1312, 42132)

        // When
        val firstSize = spyList.size
        val secondSize = spyList.size
        val thirdSize = spyList.size

        // Then
        assertEquals(1312, firstSize)
        assertEquals(42132, secondSize)
        assertEquals(1, thirdSize)
    }

    @Test
    fun givenMatchers_whenUsingThem_thenExpectCorrectBehaviour() {
        // Given
        val initialSize = spyList.size
        every {
            spyList.add(any())
        }.returnsMany(false)

        // When
        val firstAdd = spyList.add("mocked")
        val secondAdd = spyList.add("mocked2")

        // Then
        assertEquals(false, firstAdd)
        assertEquals(true, secondAdd)
        assertEquals(initialSize + 1, spyList.size)
    }

    @Test
    fun givenCallNotMatchingStub_whenCallingWithDifferentArguments_thenExpectFallbackToRealImplementation() {
        // Given
        every {
            spy.functionWithManyArgumented(string = "hej", int = 1, long = 10, list = listOf("hello there"))
        }.returns("stubbed")

        // When
        val output = spy.functionWithManyArgumented(string = "hej", int = 1, long = 10, list = listOf("hello there", "I am not stubbed"))

        // Then
        assertEquals("hej 1 10 [hello there, I am not stubbed]", output)
    }

    @Test
    fun givenCallMatchingStub_whenCallingWithDifferentArguments_thenExpectFallbackToRealImplementation() {
        // Given
        every {
            spy.functionWithManyArgumented(string = "hej", int = 1, long = 1, list = listOf("hello there"))
        }.returns("stubbed")

        // When
        val output = spy.functionWithManyArgumented(string = "hej", int = 1, long = 1, list = listOf("hello there"))

        // Then
        assertEquals("stubbed", output)
    }
}