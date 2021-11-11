package io.mockative

import kotlinx.coroutines.CoroutineScope
import kotlin.test.Test
import kotlin.test.assertEquals

class GitHubServiceMocksTest {

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsUsed() = dispatchTest { service, api ->
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository = Repository(id, "Mockative")

        given(api).coroutine { repository(id) }
            .thenReturn(repository)

        // when
        val result = service.repository(id)

        // then
        assertEquals(repository, result)
    }

    @Mocks(GitHubAPI::class)
    private fun dispatchTest(block: suspend CoroutineScope.(GitHubService, GitHubAPI) -> Unit) = dispatchBlockingTest {
        val dispatchers = ApplicationDispatchers.Unconfined

        val github = mock(GitHubAPI::class)
        val service = GitHubService(github, dispatchers)
        block(service, github).also {
            validate(github)
        }
    }
}