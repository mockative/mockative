package io.github

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.mock
import kotlin.test.*

class DefaultInvocationsTests {
    @Mock
    private val subject = mock(classOf<DefaultInvocationTestSubject>())

    @Mock
    private val otherSubject = mock(classOf<DefaultInvocationTestSubject>())

    @Test
    fun whenCheckingEqualityWithOtherInstance_thenResultIsFalse() {
        @Suppress("ReplaceCallWithBinaryOperator")
        assertFalse(subject.equals(otherSubject))
    }

    @Test
    fun whenCheckingEqualityWithSameInstance_thenResultIsTrue() {
        @Suppress("ReplaceCallWithBinaryOperator")
        assertTrue(subject.equals(subject))
    }

    @Test
    fun hashCodeIsNotEqualToHashCodeOfOtherInstance() {
        assertNotEquals(subject.hashCode(), otherSubject.hashCode())
    }

    @Test
    fun hashCodeIsEqualToHashCodeOfSameInstance() {
        assertEquals(subject.hashCode(), subject.hashCode())
    }

    @Test
    fun toStringIsNotEqualToToStringOfOtherInstance() {
        assertNotEquals(subject.toString(), otherSubject.toString())
    }

    @Test
    fun toStringIsEqualToToStringOfSameInstance() {
        assertEquals(subject.toString(), subject.toString())
    }
}
