# Controlling generation of mocks in Mockative 3

To enable support of mock type generation through KSP in Mockative 3, special consideration had to be made to ensure 
the least amount of friction when writing tests, while not polluting production builds with generated mock classes. 
This is achieved through a Gradle plugin that conditionally enables code generation depending on various conditions:

1. Code generation is disabled complete if the Gradle property `io.mockative.disabled=true` is included. This is to 
   provide developers with a way to ensure generated code does not end up in production builds, should any of the 
   following steps prevent that.
   1. This is also the only way to disable the application of the `all-open` plugin on types annotated with `@Mockable`.
2. Code generation is enabled if one of these conditions are fulfilled:
   1. The Gradle property `io.mockative.enabled=true` is included.
   2. If a `test` or `verification` task is detected.
   3. If a `test[...]UnitTest` task is executed (Android unit tests)
   4. If a `connected[...]AndroidTest` task is detected (Android instrumented tests).

Should any of the heuristics mentioned in point 2 not work, please open an issue so we can have a look at supporting 
that use case.
