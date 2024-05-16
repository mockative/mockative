package io.github

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.isMock
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class ClassWithNestedPropertiesTests {
    @Mock
    val mock: ClassWithNestedProperties = mock(classOf<ClassWithNestedProperties>())

    @Test
    fun isMockEmptyClass() {
        assertTrue(isMock(mock))
    }
}
