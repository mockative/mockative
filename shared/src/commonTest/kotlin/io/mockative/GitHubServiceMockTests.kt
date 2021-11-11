package io.mockative

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GitHubServiceMockTests {

    @Mock private val github = mock(GitHubAPI::class)

    private val service = GitHubService(github, ApplicationDispatchers.Unconfined)

    @AfterTest
    fun verifyMocks() {
        validateMocks()
    }

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsUsed() = runBlockingTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository = Repository(id, "Mockative")

        givenSuspend(github) { repository(id) }
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

        given(github) { repository(id) }
            .thenReturn(repository)

        // when
        val result = service.repository(id)

        // then
        assertEquals(repository, result)
    }
}