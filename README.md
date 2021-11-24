# Mockative

![Maven Central](https://img.shields.io/maven-central/v/io.mockative/mockative)

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

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation("io.mockative:mockative:1.0.4")
            }
        }
    }
}

dependencies {
    ksp(implementation("io.mockative:mockative-processor:1.0.4"))
}
```

## Installation for JVM projects

### build.gradle.kts

```kotlin
plugins {
    id("com.google.devtools.ksp")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.mockative:mockative-jvm:1.0.4")
    
    ksp(implementation("io.mockative:mockative-processor-jvm:1.0.4"))
}
```

## Testing with Mockative

To mock a given method on an interface, annotate a property holding the interface type with the
`@Mock` annotation, and assign it to the result of a call to the `<T> mock(KClass<T>)` function:

```kotlin
class GitHubServiceTests {
    @Mock
    val api = mock(classOf<GitHubAPI>())
}
```

Then, to stub a function or property on the mock there is a couple of options:

### Stubbing using Expressions

It is possible to stub a function or property by invoking it through the use of either
the `<R> invocation(T.() -> R)` or `<R> coroutine(T.() -> R)` function available from
the `<T> given(T)` function:

```kotlin
// Stub a `suspend` function
given(mock).coroutine { fetchData("mockative/mockative") }
    .then { data }

// Stub a blocking function
given(mock).invocation { transformData(data) }
    .then { transformedData }

// Stub a property getter
given(mock).invocation { mockProperty }
    .then { mockPropertyData }

// Stub a property setter
given(mock).invocation { mockProperty = transformedData }
    .thenDoNothing()
```

### Stubbing using Callable References

Callable references allows you to match arguments on something other than equality:

```kotlin
// Stub a `suspend` function
given(mock).suspendFunction(mock::fetchData)
    .whenInvokedWith(oneOf("mockative/mockative", "mockative/mockative-processor"))
    .then { data }

// Stub a blocking function
given(mock).function(mock::transformData)
    .whenInvokedWith(any())
    .then { transformedData }

// Stub a property getter
given(mock).getter(mock::mockProperty)
    .whenInvoked()
    .then { mockPropertyData }

// Stub a property setter
given(mock).setter(mock::mockProperty)
    .whenInvokedWith(matching { it.name == "foo" })
    .thenDoNothing()

// When the function being stubbed has overloads with a different number of arguments, a specific
// overload can be selected using one of the `fun[0-9]` functions.
given(mock).function(mock::transformData, fun0())
    .whenInvokedWith(any())
    .then { transformedData }

// When the function being stubbed has overloads with the same number of arguments, but different
// types, the type arguments must be specified using one of the `fun[0-9]` functions.
given(mock).function(mock::transformData, fun2<Data, String>())
    .whenInvokedWith(any(), any())
    .then { transformedData }

// Additionally, you can stub functions and properties by their name, but in this case you'll
// need to provide type information for the matchers.
given(mock).function("transformData")
    .whenInvokedWith(any<Data>())
    .then { transformedData }
```

### Stubbing implementations

Both expressions using `given` and callable references using `when[...]` supports the same API for
stubbing the implementation, through the use of the `then` functions.

| Function                           | Description                                                                                                |
| ---------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| `then(block: (P1, P..., PN) -> R)` | Invokes the specified block. The arguments passed to the block are the arguments passed to the invocation. |
| `thenInvoke(block: () -> R)`       | Invokes the specified block.                                                                               |
| `thenReturn(value: R)`             | Returns the specified value.                                                                               |
| `thenThrow(throwable: Throwable)`  | Throws the specified exception.                                                                            |

When the return type of the function or property being stubbed is `Unit`, the following additional
then function is available:

| Function          | Description     |
| ----------------- | --------------- |
| `thenDoNothing()` | Returns `Unit`. |

The untyped callable references using `<T : Any> whenInvoking(T, String)` and
`<T : Any> whenSuspending(T, String)` supports the following additional `then` function:

| Function                                   | Description                                                                                                      |
| ------------------------------------------ | ---------------------------------------------------------------------------------------------------------------- |
| `then(block: (args: Array<Any?>) -> Any?)` | Invokes the specified block. The argument passed to the block is an array of arguments passed to the invocation. |

## Generic Types

When mocking a generic type, use the `<T> classOf(): KClass<T>` function to retain the type 
arguments when passed to the `<T> mock(KClass<T>)` function.

```kotlin
class GenericTypeTest {
    @Mock
    val list = mock(classOf<List<String>>())
}
```

## Verification

Verification of invocations to mocks is supported through the `verify(mock)` API:

### Verification using Expressions

```kotlin
// Expression (suspend function)
verify(mock).coroutine { fetchData("mockative/mockative") }
    .wasNotInvoked()

// Expression (blocking function)
verify(mock).invocation { transformData(data) }
    .wasInvoked(atLeast = 1.time)

// Expression (property getter)
verify(mock).invocation { mockProperty }
    .wasInvoked(atLeast = once, atMost = 6.times)

// Expression (property setter)
verify(mock).invocation { mockProperty = transformedData }
    .wasInvoked(exactly = 9.times)
```

### Verification using Callable References

```kotlin
// Function Reference (suspend function)
verify(mock).coroutine(mock::fetchData)
    .with(eq("mockative/mockative"))
    .wasNotInvoked()

// Function Reference (blocking function)
verify(mock).function(mock::transformData)
    .with(any())
    .wasInvoked(atMost = 3.times)

// Getter
verify(mock).getter(mock::mockProperty)
    .wasInvoked(exactly = 4.times)

// Setter
verify(mock).setter(mock::mockProperty)
    .with(any())
    .wasInvoked(atLeast = 7.times)
```

## Validation

```kotlin
// Verifies that all expectations were verified through a call to `verify(mock).wasInvoked()`.
verify(mock).hasNoUnverifiedExpectations()

// Verifies that the mock has no expectations that weren't invoked at least once.
verify(mock).hasNoUnmetExpectations()
```
