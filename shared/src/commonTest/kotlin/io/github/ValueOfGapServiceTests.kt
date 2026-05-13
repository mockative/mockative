package io.github

import io.mockative.any
import io.mockative.every
import io.mockative.mock
import io.mockative.verify
import kotlin.test.Test

class ValueOfGapServiceTests {

    val service = mock(ValueOfGapService::class)

    // --- Enum ---

    @Test
    fun stubWithAnyEnum() {
        every { service.acceptEnum(any()) }.returns(Unit)
        service.acceptEnum(Color.RED)
        verify { service.acceptEnum(any()) }.wasInvoked()
    }

    @Test
    fun stubReturnEnum() {
        every { service.returnEnum() }.returns(Color.GREEN)
        val result = service.returnEnum()
        verify { service.returnEnum() }.wasInvoked()
    }

    // --- Object ---

    @Test
    fun stubWithAnyObject() {
        every { service.acceptObject(any()) }.returns(Unit)
        service.acceptObject(Singleton)
        verify { service.acceptObject(any()) }.wasInvoked()
    }

    // --- Inline/value class ---

    @Test
    fun stubWithAnyInlineClass() {
        every { service.acceptInlineClass(any()) }.returns(Unit)
        service.acceptInlineClass(UserId("test-id"))
        verify { service.acceptInlineClass(any()) }.wasInvoked()
    }

    @Test
    fun stubReturnInlineClass() {
        every { service.returnInlineClass() }.returns(UserId("test-id"))
        val result = service.returnInlineClass()
        verify { service.returnInlineClass() }.wasInvoked()
    }

    // --- Non-@Mockable interface ---

    @Test
    fun stubWithAnyNonMockableInterface() {
        every { service.acceptNonMockableInterface(any()) }.returns(Unit)
        service.acceptNonMockableInterface(object : NonMockableInterface {
            override fun doSomething() = "test"
        })
        verify { service.acceptNonMockableInterface(any()) }.wasInvoked()
    }

    // --- Abstract with constructor ---

    @Test
    fun stubWithAnyAbstractWithConstructor() {
        every { service.acceptAbstractWithConstructor(any()) }.returns(Unit)
        val concrete = object : AbstractWithConstructor("test") {
            override fun compute() = 42
        }
        service.acceptAbstractWithConstructor(concrete)
        verify { service.acceptAbstractWithConstructor(any()) }.wasInvoked()
    }

    // --- Deep sealed hierarchy ---

    @Test
    fun stubWithAnyDeepSealed() {
        every { service.acceptDeepSealed(any()) }.returns(Unit)
        service.acceptDeepSealed(DeepSealedRoot.Level1A.Level2A.Leaf("test"))
        verify { service.acceptDeepSealed(any()) }.wasInvoked()
    }

    // --- Sealed class ---

    @Test
    fun stubWithAnySealedClass() {
        every { service.acceptSealedClass(any()) }.returns(Unit)
        service.acceptSealedClass(SealedClass.DataVariant(42))
        verify { service.acceptSealedClass(any()) }.wasInvoked()
    }

    // --- Function types ---

    @Test
    fun stubWithAnyFunction0() {
        every { service.acceptFunction0(any()) }.returns(Unit)
        service.acceptFunction0 { }
        verify { service.acceptFunction0(any()) }.wasInvoked()
    }

    @Test
    fun stubWithAnyFunction1() {
        every { service.acceptFunction1(any()) }.returns(Unit)
        service.acceptFunction1 { it.length }
        verify { service.acceptFunction1(any()) }.wasInvoked()
    }

    @Test
    fun stubWithAnySuspendFunction() {
        every { service.acceptSuspendFunction(any()) }.returns(Unit)
        service.acceptSuspendFunction { "test" }
        verify { service.acceptSuspendFunction(any()) }.wasInvoked()
    }

    // --- Nullable custom types ---

    @Test
    fun stubWithAnyNullableEnum() {
        every { service.acceptNullableEnum(any()) }.returns(Unit)
        service.acceptNullableEnum(Color.BLUE)
        verify { service.acceptNullableEnum(any()) }.wasInvoked()
    }

    @Test
    fun stubWithAnyNullableInterface() {
        every { service.acceptNullableInterface(any()) }.returns(Unit)
        service.acceptNullableInterface(null)
        verify { service.acceptNullableInterface(any()) }.wasInvoked()
    }
}
