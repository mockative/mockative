package io.github

import io.mockative.classOf
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertNotNull

class UseCaseTests {
    private val useCase = mock(classOf<UseCase<Int, String>>())

    @Test
    fun foo() {
        assertNotNull(useCase)
    }
}
