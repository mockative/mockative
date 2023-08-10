package io.mockative

import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

internal class GitHubServiceMockTests {

    @Mock val github = mock(classOf<GitHubAPI>())
    @Mock val expected = mock(classOf<ExpectedAPI>())
    @Mock val nested = mock(classOf<GitHubService.NestedAPI>())

    private val service = GitHubService(github, ApplicationDispatchers.Unconfined)

    @AfterTest
    fun validateMocks() {
        verifyNoUnmetExpectations(github)
    }

    @Test
    fun whenCallingCreate_thenCreateIsCalled() = runTest {
        // given
        val repository = Repository(id = "mockative/mockative", name = "Mockative")

        // when
        service.create(repository)

        // then
        coVerify { github.create(repository) }
            .wasInvoked(atLeast = once)
    }

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsUsed() = runTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository = Repository(id, "Mockative")

        coGiven { github.repository(id) }
            .thenReturn(repository)

        // when
        val result = service.repository(id)

        // then
        assertEquals(repository, result)
    }

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsUsed_dispatched() = runTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository = Repository(id, "Mockative")

        coGiven { github.repository(eq(id)) }
            .thenReturn(repository)

        // when
        val result = service.repository(id)

        // then
        assertEquals(repository, result)
    }

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsCalled() = runTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository = Repository(id, "Mockative")

        coGiven { github.repository(id) }
            .thenReturn(repository)

        // when
        service.repository(id)

        // then
        coVerify { github.repository(id) }
            .wasInvoked(exactly = once)

        verifyNoUnmetExpectations(github)
    }

    @Test
    fun givenSetupOfSuspendingCommandToThrow_whenCallingCommand_thenMockIsCalled() = runTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"

        coGiven { github.repository(id) }
            .thenThrow(Error("Expected exception"))

        // when
        val actual = runCatching { service.repository(id) }

        // then
        assertNotNull(actual.exceptionOrNull())

        coVerify { github.repository(id) }
            .wasInvoked(exactly = once)
    }

    @Test
    fun testStubOverrides() = runTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val mockative = Repository(id, "Mockative")

        coGiven { github.repository(id) }
            .thenReturn(mockative)

        val firstRepository = service.repository(id)

        assertSame(mockative, firstRepository)

        coVerify { github.repository(id) }
            .wasInvoked(exactly = once)

        val mockito = Repository(id, "Mockito")
        coGiven { github.repository(id) }
            .thenReturn(mockito)

        // When
        val secondRepository = service.repository(id)

        // Then
        assertSame(mockito, secondRepository)

        coVerify { github.repository(id) }
            .wasInvoked(exactly = once)
    }

    @Test
    fun testDefaultMatchers() {
        given { github.thing(any(""), eq(3), anyRepository()) }
            .thenDoNothing()

        github.thing("a", 3, Repository("mockative", "mockative"))

        verify { github.thing(any(""), eq(3), anyRepository()) }
            .wasInvoked()
    }
}

fun anyRepository(): Repository {
    return Repository("", "")
}
