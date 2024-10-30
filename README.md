# Mockative

[ksp]: https://github.com/google/ksp

[![Maven Central](https://img.shields.io/maven-central/v/io.mockative/mockative)](https://search.maven.org/artifact/io.mockative/mockative)

> [!IMPORTANT]  
> Kotlin 2 support is coming. See (https://github.com/mockative/mockative/pull/131)

Mocking for Kotlin/Native and Kotlin Multiplatform using the Kotlin Symbol Processing API ([KSP]).
Notable features include:

- Concise, non-intrusive, type-safe API
- Mocking of classes and interfaces
- Supports both [values](#stubbing-using-values) and [matchers](#stubbing-using-matchers) when
  during stubbing [verification](#verification)
- Supports [implicit stubbing](#implicit-stubbing-of-functions-returning-unit) of functions
  returning `Unit`

## Installation for Multiplatform projects

Add the `io.mockative` plugin to your **build.gradle.kts** file:

```kotlin
plugins {
    id("io.mockative")
}
```

Then add the following to your **gradle.properties** file:

```properties
#KSP
ksp.useKSP2=true
```

If you're having the following error:

> KSP2: KtInvalidLifetimeOwnerAccessException: Access to invalid KtAlwaysAccessibleLifetimeToken: PSI has changed since creation

Please disable incremental processing for KSP by adding the following to your **gradle.properties** file:

```properties
ksp.incremental=false
```

### Making types mockable

To mock an interface or class with Mockative, annotate the type with the `@Mockable` annotation. This annotation
tells Mockative to generate a mock implementation of the annotated type, which can be used in tests.

```kotlin
import io.mockative.Mockable

@Mockable
class MyService {
    // ...
}
```

The Kotlin `all-open` plugin will automatically be applied to any `class` annotated with `@Mockable`.

#### Mocking External Types

Sometimes we would like to mock types that are external to our module in our tests. To do so, pass a list of types 
you'd like to mock in **any** `@Mockable` annotation in your main module. If you add the list of types to the 
`@Mockable` annotation of a type you'd also like to mock by itself, you must specify the type itself in the list of 
types to mock. By not specifying any types in the annotation, a mock for the annotated type itself is generated.

```kotlin
import io.mockative.Mockable

@Mockable(MyService::class, Clock::class)
class MyService {
    // ...
}
```

## Testing with Mockative

Obtaining an instance of a mock is as ease as calling the `<T> mock(KClass<T>)` function:

```kotlin
class GitHubServiceTests {
    val api = mock(classOf<GitHubAPI>())
}
```

Then, to stub a function or property on the mock you have a couple of options:

### Stubbing using values

To begin stubbing a function you may simply pass the values to the function call inside a block
passed to the `every` or `coEvery` (when stubbing a `suspend` function) functions:

```kotlin
// Stub a blocking function
every { githubApi.getRepository("mockative/mockative") }
    .invokes { response }

// Stub a `suspend` function (notice the use of `coEvery`)
coEvery { repositoryMapper.mapResponseToRepository(response) }
    .invokes { repository }

// Stub a property getter
every { repository.maintainer }
    .returns("Nillerr")

// Stub a property setter (these are stubbed by default)
every { repository.stars = repository.stars + 1 }
    .doesNothing()
```

### Stubbing using matchers

Sometimes when stubbing a function we're faced with difficulties providing a specified value for
one or more of the parameters of our expectation. In such cases we can use the matcher API to
specify the values our stub accepts:

```kotlin
// Assuming we want to stub the function in this S3Client:
interface S3Client {
    suspend fun <T> getObject(input: GetObjectRequest, block: suspend (GetObjectResponse) -> T): T
}

// Providing a value for the `block` parameter is difficult, so we can use the `any()` matcher to
// specify that this expectation will match any value passed to that parameter. When we use one 
// matcher, we must use matchers for every parameter, and as such we must use the `eq(value)` 
// matcher to specify that we're expecting a specific request.
coEvery { s3Client.getObject<File>(eq(request), any()) }
    .returnsMany(expected)
```

Also note how we're explicitly specifying the type parameter. We could do the same by explicitly
specifying it in the call to `any()` like this:

```kotlin
any<suspend (GetObjectResponse) -> File>()
```

But that would be significantly more verbose that the alternative.

> ❕Mockative has limited support for the matcher API when targeting Kotlin/Wasm (WASI).

### Stubbing using functions

You may want to provide a function as an argument to another function, where you would like to be
able to record invocations on that function. To do that, Mockative includes the interfaces `Fun[N]`
and `CoFun[N]` (for `suspend` functions), each declaring a single function `invoke` that you can
pass as a mock to other functions in order to stub and verify invocations on it:

```kotlin
// Declare the mock function as a property in your test class
@Mock
val block = mock(classOf<Fun1<GetObjectResponse, File>>())

// Stub the mock function
every { block.invoke(response) }
    .returns(file)

// Call something that calls the mock function
s3Client.getObject(request, block::invoke)

// Verify the call to the mock function
verify { block.invoke(response) }
    .wasInvoked(exactly = once)
```

### Stubbing implementations

The following functions are available to provide a stub implementation for every expectation:

| Function                                              | Description                                                                                                                                                                                                                      |
|-------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `invokes(block: (args: Array<Any?>) -> R)`            | Invokes the specified block. The arguments passed to the block are the arguments passed to the invocation.                                                                                                                       |
| `invokes(block: () -> R)`                             | Invokes the specified block.                                                                                                                                                                                                     |
| `invokesMany(vararg block: (args: Array<Any?>) -> R)` | Invokes the specified blocks in sequence. The arguments passed to the block are the arguments passed to the invocation. Once the last block in the sequence has been invoked, this stubbing will no longer match any invocation. |
| `invokesMany(vararg blocks: () -> R)`                 | Invokes the specified block. Once the last block in the sequence has been invoked, this stubbing will no longer match any invocation.                                                                                            |
| `returns(value: R)`                                   | Returns the specified value.                                                                                                                                                                                                     |
| `returnsMany(vararg value: R)`                        | Returns the specified values in sequence. Once the last value in the sequence has been returned, this stubbing will no longer match any invocation.                                                                              |
| `throws(throwable: Throwable)`                        | Throws the specified exception.                                                                                                                                                                                                  |
| `throwsMany(throwable: Throwable)`                    | Throws the specified exceptions in sequence. Once the last exception in the sequence has been thrown, this stubbing will no longer match any invocation.                                                                         |

In order to provide familiarity to developers coming from MockK, who prefer using infix notation, 
Mockative also supports infix notation for the `invokes`, `returns`, and `throws` functions:

```kotlin
every { api.getUsers() } returns users
```

When the return type of the function or property being stubbed is `Unit`, the following additional
then function is available:

| Function        | Description     |
|-----------------|-----------------|
| `doesNothing()` | Returns `Unit`. |

### Implicit stubbing of functions returning Unit

Mockative automatically stubs functions returning `Unit`, based on the idea that such functions are
typically used for verification rather than stubbing, and stubbing them could thus be considered
boilerplate, while they are trivially automatically stubbed.

You can opt out of this behaviour on the project level through your **build.gradle.kts** file:

**build.gradle.kts**

```kotlin
mockative {
    stubsUnitByDefault = false
}
```

Alternatively, you can opt out (or opt-in if you've opted out on the project level), using the
`configure(mock, block)` function either inline:

```kotlin
@Mock
val api = configure(mock(classOf<GitHubAPI>())) { stubsUnitByDefault = false }
```

Or as needed:

```kotlin
@Mock
val api = mock(classOf<GitHubAPI>())

@Test
fun test() {
    configure(api) { stubsUnitByDefault = false }
}
```

The configuration is stored on the mock instance. It must be configured before receiving any
invocations.

## Generic Types

When mocking a generic type use the `<T> classOf(): KClass<T>` function to retain the type
arguments when passed to the `<T> mock(KClass<T>)` function. You can use the `classOf` function
regardless of whether you're mocking a generic or non-generic type.

```kotlin
class GenericTypeTest {
    @Mock
    val list = mock(classOf<List<String>>())
}
```

## Verification

Verification of invocations on mocks is supported through the `verify` and `coVerify` functions
using either values or matchers as per the stubbing (`every`) API:

### Verification using values

```kotlin
// Verify a `suspend` function (notice the use of `coVerify`)
coVerify { githubApi.getRepository("mockative/mockative") }
    .wasNotInvoked()

// Verify a blocking function
verify { repositoryMapper.mapResponseToRepository(response) }
    .wasInvoked(atLeast = 1)

// Verify a property getter
verify { repository.maintainer }
    .wasInvoked(atLeast = once, atMost = 6)

// Verify a property setter
verify { repository.stars = repository.stars + 1 }
    .wasInvoked(exactly = 1)
```

### Verification using matchers

```kotlin
// Verify a suspend function (notice the use of `coVerify`)
coVerify { s3Client.getObject<File>(eq(request), any()) }
    .wasInvoked(exactly = once)
```

## Validation

In addition to verification of expectations there's also validation of the use of expectations,
which, if not used, can lead to both "under verification" and "over mocking" resulting in
inaccurate tests. To handle such cases Mockative provides 2 functions you can use on your mocks,
usually in the `@AfterTest` function of your tests:

```kotlin
@AfterTest
fun afterTest() {
    // Verifies that all expectations were verified through a call to `verify { ... }.wasInvoked()`.
    verifyNoUnverifiedExpectations(githubApi)

    // Verifies that the mock has no expectations that weren't invoked at least once.
    verifyNoUnmetExpectations(s3Client)
}
```
