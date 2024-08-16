package io.github

import io.mockative.MissingExpectationException
import io.mockative.Mock
import io.mockative.classOf
import io.mockative.mock
import io.mockative.verify
import kotlin.test.Test
import kotlin.test.assertFailsWith

class MockWithNestedMockWithUnitReturningFunctionTests {
    @Mock
    val mockedClassWithProperty = mock(classOf<ClassWithProperty>())

    @Test
    fun stubs_units_by_default_works_for_recursive_mock() {
        verify { mockedClassWithProperty.propertyWithUnitReturningFunction.doSomethingReturnUnit() }.wasInvoked(1)
    }

    @Test
    fun stubs_units_by_default_doesnt_affect_function_not_returning_unit() {
        assertFailsWith<MissingExpectationException> { mockedClassWithProperty.propertyWithUnitReturningFunction.returnSomeString() }
    }
}
