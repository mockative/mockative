package io.github

import io.mockative.Fun0
import io.mockative.Mock
import io.mockative.any
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.doesNothing
import io.mockative.eq
import io.mockative.every
import io.mockative.mock
import io.mockative.once
import io.mockative.verify
import io.mockative.verifyNoUnmetExpectations
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

internal class GitHubServiceMockTests {

    @Mock
    val github: GitHubAPI = mock(classOf<GitHubAPI>())

    @Mock
    val expected: ExpectedAPI = mock(classOf<ExpectedAPI>())

    @Mock
    val nested: GitHubService.NestedAPI = mock(classOf<GitHubService.NestedAPI>())

    @Mock
    val configuration: GitHubConfiguration = mock(classOf<GitHubConfiguration>())

    @Mock
    val function: Fun0<Unit> = mock(classOf<Fun0<Unit>>())


    private val service = GitHubService(github, configuration, ApplicationDispatchers.Unconfined)

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

        coEvery { github.repository(id) }
            .returnsMany(repository)

        // when
        val result = service.repository(id)

        // then
        assertEquals(repository, result)
    }

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsUsed_invokesMany() = runTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository1 = Repository(id, "Mockative")
        val repository2 = Repository(id, "Kontinuity")

        coEvery { github.repository(id) }
            .invokesMany({ repository1 }, { repository2 })

        // when
        val result1 = service.repository(id)
        val result2 = service.repository(id)

        // then
        assertEquals(repository1, result1)
        assertEquals(repository2, result2)
    }

    @Test
    fun givenSetupOfSuspendingCommand_whenCallingCommand_thenMockIsUsed_dispatched() = runTest {
        // given
        val id = "0efb1b3b-f1b2-41f8-a1d8-368027cc86ee"
        val repository = Repository(id, "Mockative")

        coEvery { github.repository(eq(id)) }
            .returnsMany(repository)

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

        coEvery { github.repository(id) }
            .returnsMany(repository)

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

        coEvery { github.repository(id) }
            .throwsMany(Error("Expected exception"))

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

        coEvery { github.repository(id) }
            .returnsMany(mockative)

        val firstRepository = service.repository(id)

        assertSame(mockative, firstRepository)

        coVerify { github.repository(id) }
            .wasInvoked(exactly = once)

        val mockito = Repository(id, "Mockito")
        coEvery { github.repository(id) }
            .returnsMany(mockito)

        // When
        val secondRepository = service.repository(id)

        // Then
        assertSame(mockito, secondRepository)

        coVerify { github.repository(id) }
            .wasInvoked(exactly = once)
    }

    @Test
    fun getToken() {
        // Given
        val token = "the-token"
        every { configuration.token }.returns(token)

        // When
        val result = service.getToken()

        // Then
        assertEquals(token, result)
    }

    @Test
    fun setToken() {
        // Given
        val token = "the-token"
        every { configuration.token = token }.doesNothing()

        // When
        service.setToken(token)

        // Then
        verify { configuration.token = token }
            .wasInvoked()
    }

    @Test
    fun setTokenWithAny() {
        // Given
        val token = "the-token"
        every { configuration.token = any() }.doesNothing()

        // When
        service.setToken(token)

        // Then
        verify { configuration.token = token }
            .wasInvoked()
    }

    @Test
    fun testDefaultMatchers() {
//        given(github).function(github::thing)
//            .whenInvokedWith(p2 = eq(3))
//            .thenDoNothing()
//
//        github.thing("a", 3, Repository("mockative", "mockative"))
//
//        verify(github).function(github::thing)
//            .with(p2 = eq(3))
//            .wasInvoked()
    }
}
