package io.github

import io.mockative.Fun0
import io.mockative.Mock
import io.mockative.any
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.eq
import io.mockative.every
import io.mockative.justRun
import io.mockative.mock
import io.mockative.never
import io.mockative.once
import io.mockative.verify
import io.mockative.checkUnnecessaryStub
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
    val configuration: GitHubConfiguration = mock(classOf<GitHubConfiguration>())

    @Mock
    val function: Fun0<Unit> = mock(classOf<Fun0<Unit>>())


    private val service = GitHubService(github, configuration, ApplicationDispatchers.Unconfined)

    @AfterTest
    fun validateMocks() {
        checkUnnecessaryStub(github)
    }

    @Test
    fun whenCallingCreate_thenCreateIsCalled() = runTest {
        // given
        val repository = Repository(id = "mockative/mockative", name = "Mockative")

        // when
        service.create(repository)

        // then
        coVerify { github.create(repository) }
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
        coVerify(exactly = once) { github.repository(id) }

        checkUnnecessaryStub(github)
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

        coVerify(exactly = once) { github.repository(id) }
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

        coVerify(exactly = once) { github.repository(id) }

        val mockito = Repository(id, "Mockito")
        coEvery { github.repository(id) }
            .returnsMany(mockito)

        // When
        val secondRepository = service.repository(id)

        // Then
        assertSame(mockito, secondRepository)

        coVerify(exactly = once) { github.repository(id) }
    }

    @Test
    fun getToken() {
        // Given
        val token = "the-token"
        every { configuration.token } returns token

        // When
        val result = service.getToken()

        // Then
        assertEquals(token, result)
    }

    @Test
    fun setToken() {
        // Given
        val token = "the-token"
        justRun { configuration.token = token }

        // When
        service.setToken(token)

        // Then
        verify { configuration.token = token }
    }

    @Test
    fun setTokenWithAny() {
        // Given
        val token = "the-token"
        justRun { configuration.token = any() }

        // When
        service.setToken(token)

        // Then
        verify { configuration.token = token }
    }

    @Test
    fun testNeverCalled() = runTest {
        coVerify(exactly = never) { github.repository(any()) }
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
