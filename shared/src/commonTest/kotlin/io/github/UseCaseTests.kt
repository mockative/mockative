package io.github

import io.mockative.of
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertNotNull

class UseCaseTests {
    private val useCase = mock(of<UseCase<Int, String>>())

    @Test
    fun foo() {
        assertNotNull(useCase)
    }
}
