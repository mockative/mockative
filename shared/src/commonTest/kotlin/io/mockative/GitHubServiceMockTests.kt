package io.mockative

import kotlin.test.*

internal class GitHubServiceMockTests {

    @Mock val github = mock(classOf<GitHubAPI>())
    @Mock val expected = mock(classOf<ExpectedAPI>())
    @Mock val nested = mock(classOf<GitHubService.NestedAPI>())

    private val service = GitHubService(github, ApplicationDispatchers.Unconfined)

    @AfterTest
    fun validateMocks() {
        verify(github).hasNoUnmetExpectations()
    }

    @Test
    fun whenCallingCreate_thenCreateIsCalled() = runBlockingTest {
        // given
        val repository = Repository(id = "mockative/mockative", name = "Mockative")

        // when
        service.create(repository)

        // then
        verify(github).coroutine { create(repository) }
            .wasInvoked(atLeast = once)
    }

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsUsed() = runBlockingTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository = Repository(id, "Mockative")

        given(github).coroutine { repository(id) }
            .thenReturn(repository)

        // when
        val result = service.repository(id)

        // then
        assertEquals(repository, result)
    }

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsUsed_dispatched() = dispatchBlockingTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository = Repository(id, "Mockative")

        given(github).coroutine { repository(id) }
            .thenReturn(repository)

        // when
        val result = service.repository(id)

        // then
        assertEquals(repository, result)
    }

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsCalled() = runBlockingTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository = Repository(id, "Mockative")

        given(github).coroutine { repository(id) }
            .thenReturn(repository)

        // when
        service.repository(id)

        // then
        verify(github).coroutine { repository(id) }
            .wasInvoked(exactly = once)

        verify(github).hasNoUnverifiedExpectations()
    }

    @Test
    fun givenSetupOfSuspendingCommandToThrow_whenCallingCommand_thenMockIsCalled() = runBlockingTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"

        given(github).coroutine { repository(id) }
            .thenThrow(Error("Expected exception"))

        // when
        val actual = runCatching { service.repository(id) }

        // then
        assertNotNull(actual.exceptionOrNull())

        verify(github).coroutine { repository(id) }
            .wasInvoked(exactly = once)
    }

    @Test
    fun testStubOverrides() = runBlockingTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val mockative = Repository(id, "Mockative")

        given(github).coroutine { repository(id) }
            .thenReturn(mockative)

        val firstRepository = service.repository(id)

        assertSame(mockative, firstRepository)

        verify(github).coroutine { repository(id) }
            .wasInvoked(exactly = once)

        val mockito = Repository(id, "Mockito")
        given(github).coroutine { repository(id) }
            .thenReturn(mockito)

        // When
        val secondRepository = service.repository(id)

        // Then
        assertSame(mockito, secondRepository)

        verify(github).coroutine { repository(id) }
            .wasInvoked(exactly = once)
    }
}