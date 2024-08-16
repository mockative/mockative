package io.github

import io.mockative.MissingExpectationException
import io.mockative.Mock
import io.mockative.classOf
import io.mockative.every
import io.mockative.mock
import io.mockative.verify
import kotlin.test.Test
import kotlin.test.assertFailsWith

class MockWithNestedMockWithUnitReturningFunctionTests {
    @Mock
    val mockedClassWithProperty = mock(classOf<ClassWithProperty>())
    val mockedClassUsedAsPropertyWithUnitReturningFunction = mock(classOf<ClassUsedAsPropertyWithUnitReturningFunction>())

    @Test
    fun stubs_units_by_default_works_for_recursive_mock() {
        every { mockedClassWithProperty.propertyWithUnitReturningFunction }.returns(mockedClassUsedAsPropertyWithUnitReturningFunction)
        mockedClassWithProperty.propertyWithUnitReturningFunction.doSomethingReturnUnit()
        verify { mockedClassWithProperty.propertyWithUnitReturningFunction.doSomethingReturnUnit() }.wasInvoked(1)
    }

    @Test
    fun stubs_units_by_default_doesnt_affect_function_not_returning_unit() {
        every { mockedClassWithProperty.propertyWithUnitReturningFunction }.returns(mockedClassUsedAsPropertyWithUnitReturningFunction)
        assertFailsWith<MissingExpectationException> { mockedClassWithProperty.propertyWithUnitReturningFunction.returnSomeString() }
    }
}
