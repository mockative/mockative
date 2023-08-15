# Migrating to Mockative 2

Mockative 2 brings with it a new API that should feel familiar to any Kotlin/JVM developers 
using [MockK](https://mockk.io/) as well as Java developers using 
[Mockito](https://site.mockito.org/), while still supporting every target of Kotlin Multiplatform.

The first step to upgrading to Mockative 2 is to update your **build.gradle.kts** file:

```kotlin
kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation("io.mockative:mockative:2.0.0")
            }
        }
    }
}

dependencies {
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, "io.mockative:mockative-processor:2.0.0")
        }
}
```

## Implicit Stubbing of Unit returning functions

Mockative 2 changes the default value of implicit stubbing of `Unit` returning functions to `true`, 
thus making the feature opt-out.

## API Changes

Once you have upgraded the version of Mockative used, you'll notice the tests of your project no 
longer compiles. This is due to the breaking changes caused by the new API introduced in 
Mockative 2. The following sections contains a couple of samples of how code written in Mockative 1 
should be written in Mockative 2.

### Coroutine

```kotlin
// Mockative 1
given(api).coroutine { fetch("mockative/mockative") }
    .thenReturn(response)

// Mockative 2
coEvery { api.fetch("mockative/mockative") }
    .returns(response)
```

**Callable Reference**

```kotlin
// Mockative 1
given(api).suspendFunction(api::fetch)
    .whenInvokedWith(any())
    .thenReturn(response)

// Mockative 2
coEvery { api.fetch(any()) }
    .returns(response)
```

### Blocking Function Call

```kotlin
// Mockative 1
given(mapper).invocation { transform(response) }
    .thenReturn(dto)

// Mockative 2
every { mapper.transform(response) }
    .returns(dto)
```

**Callable Reference**

```kotlin
// Mockative 1
given(mapper).function(mapper::transform)
    .whenInvokedWith(any())
    .thenReturn(response)

// Mockative 2
every { mapper.transform(any()) }
    .returns(response)
```

### Property Getter

```kotlin
// Mockative 1
given(config).invocation { duration }
    .thenThrow(NotFoundException("The value could not be found."))

// Mockative 2
every { config.value }
    .thenThrow(NotFoundException("The value could not be found."))
```

**Callable Reference**

```kotlin
// Mockative 1
given(config).getter(Configuration::value)
    .thenThrow(NotFoundException("The value could not be found."))

// Mockative 2
every { config.value }
    .thenThrow(NotFoundException("The value could not be found."))
```

### Property Setter

```kotlin
// Mockative 1
given(config).invocation { duration = "05:00:00" }
    .thenDoNothing()

// Mockative 2
every { config.value = "05:00:00" }
    .doesNothing()
```

**Callable Reference**

```kotlin
// Mockative 1
given(config).setter(Configuration::duration)
    .whenInvokedWith("05:00:00")
    .thenDoNothing()

// Mockative 2
every { config.value = "05:00:00" }
    .doesNothing()
```
