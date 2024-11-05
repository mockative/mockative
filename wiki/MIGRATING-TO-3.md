# Migrating to Mockative 3

Mockative 3 brings a much easier setup that removes the explicit application of `ksp` and `allopen`, by providing a 
Gradle plugin that automatically takes care of applying those plugins.

The change of major version number is not only to signal the support for a new major Kotlin version, but is 
unfortunately also meant to signal breaking changes. Kotlin 2 changed the way multiplatform source sets are compiled, 
and thus broke a crucial component of Mockative 1 and 2. To solve this, we had to rethink how Mockative generates code, 
moving it from generating code during a KSP test generation phase, to a KSP main generation phase. Changing _when_ code 
is generated was trivial, but nobody wants their test-only dependency to leak into their main code more than necessary, 
so special consideration had to be taken to ensure that doesn't happen.

Storytelling aside, these are the primary differences between Mockative 2 to Mockative 3:

 - Removed the `@Mock` annotation for properties in your test source sets
 - Added the `@Mockable` annotation for types in your main source sets
 - Introduced a new `of<T>()` function to replace `classOf<T>()` which is being deprecated.

> [!IMPORTANT]
> Mockative 3 has only been tested with Kotlin 2.0.21. It may or may not work with Kotlin versions before Kotlin 2.

## Migration Guide

> [!TIP]
> Use search and replace to fix up most of your codebase at once

To migrate from Mockative 2 to Mockative 3, these are the steps required, in order:

**1. Remove `@Mock` annotation from properties in test source sets**

```diff
-import io.mockative.Mock

class GitHubServiceTests {
-   @Mock
    val api = mock(classOf<GitHubApi>())
}
```

**2. Annotate mockable types with `@Mockable`**

```diff
+import io.mockative.Mockable

+@Mockable
interface GitHubApi {
```

**3. Run your tests**

Before running your tests you might be facing what looks like a bunch of compilation errors. Mockative 3 dynamically 
adds a dependency on a set of source files when a test task is detected. 

**4. Replace usages of `classOf<T>` with `of<T>`**

```diff
-import io.mockative.classOf
+import io.mockative.of

class GitHubServiceTests {
-   val api = mock(classOf<GitHubApi>())
+   val api = mock(of<GitHubApi>())
}
```
