package dk.nillerr.mockative

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals

class GitHubAPIMethodTests {

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsUsed() = runTest { service, api ->
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository = Repository(id, "Mockative")

        given(api) { repository(id) }
            .thenReturn(repository)

        // when
        val result = service.repository(id)

        // then
        assertEquals(repository, result)
    }

    @Mocks(GitHubAPI::class)
    private fun runTest(block: suspend CoroutineScope.(GitHubService, GitHubAPI) -> Unit) = runBlockingTest {
        val dispatchers = ApplicationDispatchers.Unconfined

        val github = mock(GitHubAPI::class)
        val service = GitHubService(github, dispatchers)
        block(service, github).also {
            verify(github)
        }
    }
}