package io.mockative

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GitHubServiceMockTests {

    @Mock private val github = mock(GitHubAPI::class)

    private val service = GitHubService(github, ApplicationDispatchers.Unconfined)

    @AfterTest
    fun validateMocks() {
        validate(github)
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
        verify(github).coroutine.exactly(1) { repository(id) }
        confirmVerified(github)
    }
}