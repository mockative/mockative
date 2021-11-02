package dk.nillerr.mockative

import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class GitHubAPIPropertyTests {

    @Mock val github = mock(GitHubAPI::class)

    @AfterTest
    fun verifyMocks() {
        verify(github)
    }

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsUsed() = runBlockingTest {
        // given
        val repository = Repository(
            "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee",
            "Mockative"
        )

        val repositories = mutableListOf<Repository>()

        given(github) { create(repository) }
            .then { repositories.add(it[0] as Repository) }

        // when
        github.create(repository.copy())

        // then
        assertContentEquals(listOf(repository), repositories)
    }
}