package io.mockative

class UseCaseTests {
    @Mock
    private val useCase = mock(classOf<UseCase<Int, String>>())
}
