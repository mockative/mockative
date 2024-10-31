package io.github

import io.mockative.of
import io.mockative.isMock
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class ClassWithNestedPropertiesTests {
    val mock: ClassWithNestedProperties = mock(of<ClassWithNestedProperties>())

    @Test
    fun isMockEmptyClass() {
        assertTrue(isMock(mock))
    }
}
