package io.github

import io.mockative.classOf
import io.mockative.isMock
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertTrue

internal class InternalConstructorTest {
    internal val mock: ClassWithInternalConstructor = mock(classOf<ClassWithInternalConstructor>())

    @Test
    fun isMockClassWithInternalConstructor() {
        assertTrue(isMock(mock))
    }
}
