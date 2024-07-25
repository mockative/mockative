package io.github

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.mock
import io.mockative.verify
import kotlin.test.Test

class MockWithNestedMockWithUnitReturningFunctionTests {
    @Mock
    val mockedClassWithProperty = mock(classOf<ClassWithProperty>())

    @Test
    fun does_not_match_if_one_doesnt_match() {
        verify { mockedClassWithProperty.propertyWithUnitReturningFunction.doSomethingReturnUnit() }.wasInvoked(1)
    }
}
