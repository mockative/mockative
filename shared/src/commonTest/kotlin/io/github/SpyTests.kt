package io.github

import io.mockative.any
import io.mockative.of
import io.mockative.classOf
import io.mockative.every
import io.mockative.spy
import io.mockative.spyOn
import kotlin.test.Test
import kotlin.test.assertEquals

class SpyTests {
    private val realSpy = SpyClass("realSpy")

    val spy = spy(classOf<SpyClass>(), realSpy)
    val spyList = spy(of<MutableList<String>>(), mutableListOf("real"))

    @Test
    fun givenNoStubbing_WhenSpyMethodIsInvoked_ReturnsRealImplementation() {
        // Given / When
        val spyOutput = spy.greet()
        val realOutput = realSpy.greet()

        // Then
        assertEquals(spyOutput, realOutput)
    }

    @Test
    fun givenStubbingOnSpy_whenStubbedFunctionIsCalled_ReturnsStubbedValue() {
        // Given
        every { spy.greet() }
            .returns("mocked")

        // When
        val spyOutput = spy.greet()

        // Then
        assertEquals(spyOutput, "mocked")
    }

    @Test
    fun givenStubbingGetterOverwritingInsertedElement_WhenStubbedFunctionIsInvoked_StubbedValueIsReturned() {
        // Given
        every { spyList[0] }
            .returns("mocked")

        // When
        val listOutput = spyList[0]

        // Then
        assertEquals(listOutput, "mocked")
    }

    @Test
    fun givenNoStubbingOnSpy_WhenSpyFunctionIsCalled_ThenRealImplementationIsCalled() {
        // Given
        spyList.addAll(listOf("I", "am"))

        // When
        val listOutput = spyList.reduce { acc, s -> "$acc $s" }

        // Then
        assertEquals("real I am", listOutput)
    }

    @Test
    fun givenSomeOfGettersAreStubbed_WhenInsertingValuesAtStubbedIndexes_ThenValuesAreStillStubbed() {
        // Given
        every { spyList[0] }
            .returns("mocked")

        every { spyList[2] }
            .returns("I am not am")

        // When
        spyList.addAll(listOf("hello there", "am"))
        val output = "${spyList[0]} ${spyList[1]} ${spyList[2]}"

        // Then
        assertEquals("mocked hello there I am not am", output)
    }

    @Test
    fun givenFiniteAmountOfAnswers_whenExhaustingAllStubs_thenExpectFallbackToRealImplementation() {
        // Given
        every { spyList.size }
            .returnsMany(1312, 42132)

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
        every { spyList.add(any("TODO")) }
            .returnsMany(false)

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
        every { spy.functionWithManyArgumented("hej", 1, 10, listOf("hello there")) }
            .returns("stubbed")

        // When
        val output = spy.functionWithManyArgumented("hej", 1, 10, listOf("hello there", "I am not stubbed"))

        // Then
        assertEquals("hej 1 10 [hello there, I am not stubbed]", output)
    }
}
