# Mockative

Mocking for Kotlin/Native and Kotlin Multiplatform using the Kotlin Symbol Processing API ([KSP]).

[ksp]: https://github.com/google/ksp

## Installation

Mockative uses [KSP] to generate mock classes for interfaces, and as such, it requires adding the
KSP plugin in addition to adding the runtime library and symbol processor dependencies.

### build.gradle.kts

```kotlin
plugins {
    id("com.google.devtools.ksp")
}

kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation("io.mockative:mockative:1.0.0")
            }
        }
    }
}

dependencies {
    ksp(implementation("io.mockative:mockative-processor:1.0.0"))
}
```

## Testing with Mockative

The recommended way to test with Mockative is to use the `@Mock` annotation, which requires opt-in
because it doesn't support object freezing of mocks when passing between threads when testing
suspending functions. We provide a [snippet](#coroutineskt) that enables testing coroutines while
using the `@Mock` annotation. To opt-in to the `@Mock` annotation add the following to your 
**build.gradle.kts** file:

```kotlin
kotlin {
    sourceSets {
        withType {
            if (name.endsWith("Test")) {
                languageSettings {
                    optIn("io.mockative.PropertyMocks")
                }
            }
        }
    }
}
```

To mock a given method on an interface a property holding the interface must be annotated with
the `@Mock` annotation, and be retrieved using the `mock(KClass<T>)` function:

```kotlin
class GitHubServiceTests {
    @Mock
    val api = mock(GitHubAPI::class)
}
```

Then, using the `given(receiver, block)` function, you can specify a function call, property getter
or property setter to stub, and provide an implementation using one of the `then` functions on the
expectation builder returned by `given`:

```kotlin
class GitHubServiceTests {
    @Mock
    val api = mock(GitHubAPI::class)

    val service = GitHubService(api)

    @Test
    fun testRepository() {
        // given
        val id = "mockative/mockative"
        val repository = Repository(id, "Mockative")

        given(api) { repository(id) }
            .thenReturn(repository)

        // when
        ...

        // then
        ...
    }
}
```

The available `then` functions are:

| Function             | Description                                                                                                                  |
| -------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| `then(block)`        | Invokes the `block` on every call to the expectation.                                                                        |
| `thenReturn(value)`  | Returns the specified `value` on every call to the expectation.                                                              |
| `thenSuspend(block)` | Invokes the suspending `block` on every call to the expectation. This function only works when mocking a `suspend` function. |
| `thenThrow(error)`   | Throws the specified `error` on every call to the expectation.                                                               |
| `thenDoNothing()`    | A wrapper for `thenReturn(Unit)` when the return type of the expectation is `Unit`.                                          |

### Arguments

You can access the arguments of a call to a mock using the first argument (`Array<Any?>`) in
the `then` or `thenSuspend` function:

```kotlin
class GitHubServiceTests {
    @Mock
    val api = mock(GitHubAPI::class)

    val service = GitHubService(api)

    @Test
    fun testCreateRepository() {
        // given
        val name = "Mockative"
        var recordedName: String?

        given(api) { createRepository(name) }
            .then { args ->
                recordedName = args[0] as String
            }

        // when
        service.createRepository(name)

        // then
        assertEquals(name, recordedName)
    }
}
```

The use case of this is very limited though, since Mockative doesn't provide any way to match
arguments in expectations other than equality.

## Example

Given the following API:

### GitHubAPI.kt

```kotlin
// src/commonMain/kotlin/io/mockative/GitHubAPI.kt
interface GitHubAPI {
    suspend fun repository(id: String): Repository?
}
```

And the following service:

### GitHubService.kt

```kotlin
// src/commonMain/kotlin/io/mockative/GitHubService.kt
class GitHubService(private val api: GitHubAPI) {
    suspend fun repository(id: String): Repository? {
        return api.repository(id)
    }
}
```

A test for the `GitHubService.repository(String)` method could look like this:

### GitHubServiceTests.kt

```kotlin
// src/commonTest/kotlin/io/mockative/GitHubServiceTests.kt
class GitHubServiceTests {
    @Mock
    val api = mock(GitHubAPI::class)

    val service = GitHubService(api)

    @Test
    fun givenRepositories_whenFetchingRepositories_thenRepositoriesAreReturned() {
        // given
        val id = "mockative/mockative"
        val repository = Repository(id, "Mockative")

        given(api) { repository(id) }
            .thenReturn(repository)

        // when
        val actual = service.repository(id)

        // then
        assertEquals(repository, actual)
    }
}
```

### Verification

You can verify that no unmet expectations were left in a mock using the `verify(T)` method.

```kotlin
class GitHubServiceTests {
    @Mock
    val api = mock(GitHubApi::class)

    @AfterTest
    fun verifyMocks() {
        verify(api)
    }
}
```

### Testing Coroutines

Object freezing can present a challenge when testing on Kotlin/Native using coroutines. To test
coroutines we recommend using the implementations of `runBlockingTest` we've provided below, which
supports the use of the `@Mock` annotation on properties in test classes by using `runBlocking` on
the current dispatcher / thread on iOS.

#### Coroutines.kt

```kotlin
// Common: src/commonTest/kotlin/io/mockative/Coroutines.kt
expect fun runBlockingTest(block: suspend CoroutineScope.() -> Unit)

// Android / JVM: src/androidTest/kotlin/io/mockative/Coroutines.kt
actual fun runBlockingTest(block: suspend CoroutineScope.() -> Unit) =
    runBlocking { block() }

// iOS / macOS: src/iosTest/kotlin/io/mockative/Coroutines.kt
actual fun runBlockingTest(block: suspend CoroutineScope.() -> Unit) =
    runBlocking { block() }

// JavaScript: src/jsTest/kotlin/io/mockative/Coroutines.kt
private val testScope = MainScope()

actual fun runBlockingTest(block: suspend CoroutineScope.() -> Unit): dynamic =
    testScope.promise { block() }
```

#### GitHubServiceTests.kt (Coroutines)

```kotlin
// src/commonTest/kotlin/io/mockative/GitHubServiceTests.kt
class GitHubServiceTests {
    @Mock
    val api = mock(GitHubAPI::class)

    val service = GitHubService(api)

    @Test
    fun givenRepositories_whenFetchingRepositories_thenRepositoriesAreReturned() = runBlockingTest {
        // given
        val id = "mockative/mockative"
        val repository = Repository(id, "Mockative")

        given(api) { repository(id) }
            .thenReturn(repository)

        // when
        val result = service.repository(id)

        // then
        assertEquals(repository, result)
    }
}
```

Additionally, you can use the `thenSuspend(block)` function if the mocked implementation of the
function call is itself a suspending function.

#### Running tests in a separate thread

If you've adopted a strategy of running tests in a separate thread from the thread instantiating the
test classes you will run into object freezing issues when using the `@Mock` annotation on
properties. While we recommend switching to the implementation of `runBlockingTest` we've provided
above, we also provide an alternative to enable mocking using `@Mocks(KClass<*>)` annotations.

An example of an idiomatic approach for this kind of testing is presented below:

```kotlin
// src/commonTest/kotlin/io/mockative/GitHubServiceTests.kt
class GitHubServiceTests {
    @Test
    fun givenRepositories_whenFetchingRepositories_thenRepositoriesAreReturned() =
        test { service, api ->
            // given
            val id = "mockative/mockative"
            val repository = Repository(id, "Mockative")

            given(api) { repository(id) }
                .thenReturn(repository)

            // when
            val result = service.repository(id)

            // then
            assertEquals(repository, result)
        }

    @Mocks(GitHubAPI::class)
    private fun runTest(block: suspend CoroutineScope.(GitHubService, GitHubAPI) -> Unit) =
        runBlockingTest {
            val github = mock(GitHubAPI::class)
            val service = GitHubService(github)
            block(service, github).also {
                verify(github)
            }
        }
}
```

How you use the `@Mocks(KClass<*>)` annotation is up to you. You can annotate any class or function
with it, as all it does is inform the Mockative symbol processor to generate a mock for the
specified type. The annotation is repeatable, so you can attach as many mock declarations to a class
or function as you need.

#### Multithreading

Multithreading is not supported by Mockative when targeting Kotlin/Native due to the nature of how
Mockative records invocations on expectations, which results in errors due to mocks being frozen
when crossing thread boundaries. If you're using multiple dispatchers in your code, one way to
overcome this challenge is by making the dispatchers configurable, and ensuring only a single
dispatcher is used in tests. Additionally, you may have to resort to the `@Mocks(KClass<*>)`
approach described above
in [Running tests in a separate thread](#running-tests-in-a-separate-thread), and ensure the 
dispatcher you use in the `runBlockingTest` function is the same dispatcher you use all over your 
application code, thus ensuring state never crosses thread boundaries. 
