package io.mockative

import kotlin.test.Test
import kotlin.test.assertNotNull

class UseCaseTests {
    @Mock
    private val useCase = mock(classOf<UseCase<Int, String>>())

    @Test
    fun foo() {
        assertNotNull(useCase)
    }
}
