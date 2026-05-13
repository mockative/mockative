package io.github.fake

import io.github.AbstractWithConstructor
import io.github.Color
import io.github.DeepSealedRoot
import io.github.NonMockableInterface
import io.github.SealedClass
import io.github.Singleton
import io.github.UserId
import io.mockative.fake.valueOf
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Tests for makeValueOf coverage gaps between platforms.
 *
 * JVM handles all these cases via Objenesis/Javassist/Proxy at runtime.
 * Native uses KSP-generated makeValueOf with createUninitializedInstance.
 * JS/WasmJS currently stub with Unit as T.
 *
 * Some of these tests may fail on non-JVM platforms — that's expected
 * and identifies the gaps we need to address.
 */
class ValueOfGapTests {

    // --- Enum classes ---

    @Test
    fun valueOfEnum() {
        assertNotNull(valueOf<Color>())
    }

    // --- Object types ---

    @Test
    fun valueOfObject() {
        assertNotNull(valueOf<Singleton>())
    }

    // --- Inline/value classes ---

    @Test
    fun valueOfInlineClass() {
        assertNotNull(valueOf<UserId>())
    }

    // --- Non-@Mockable interface ---

    @Test
    fun valueOfNonMockableInterface() {
        assertNotNull(valueOf<NonMockableInterface>())
    }

    // --- Abstract class with constructor parameters ---

    @Test
    fun valueOfAbstractWithConstructor() {
        assertNotNull(valueOf<AbstractWithConstructor>())
    }

    // --- Deep sealed hierarchies ---

    @Test
    fun valueOfDeepSealedRoot() {
        assertNotNull(valueOf<DeepSealedRoot>())
    }

    @Test
    fun valueOfDeepSealedLevel1A() {
        assertNotNull(valueOf<DeepSealedRoot.Level1A>())
    }

    @Test
    fun valueOfDeepSealedLevel1B() {
        assertNotNull(valueOf<DeepSealedRoot.Level1B>())
    }

    // --- Sealed class (not interface) ---

    @Test
    fun valueOfSealedClass() {
        assertNotNull(valueOf<SealedClass>())
    }

    // --- Function types ---

    @Test
    fun valueOfFunction0() {
        assertNotNull(valueOf<() -> Unit>())
    }

    @Test
    fun valueOfFunction1() {
        assertNotNull(valueOf<(String) -> Int>())
    }

    @Test
    fun valueOfSuspendFunction() {
        assertNotNull(valueOf<suspend () -> String>())
    }
}
