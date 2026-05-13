# Plan: Fix Kotlin/Native `makeValueOf` for Kotlin 2.3.20

## Problem

Kotlin 2.3.20 introduces real downcast checks at reified generic return boundaries on
Kotlin/Native. Mockative's `makeValueOf` on non-JVM platforms returns `Unit as T`, which
now throws `ClassCastException` instead of silently passing.

## Approach

Replace the `Unit as T` hack on Native with real instance creation:

1. **Concrete classes** -> `createUninitializedInstance` (bypasses constructors, like Objenesis on JVM)
2. **Sealed types** -> walk `sealedSubclasses` recursively to find a concrete leaf, then `createUninitializedInstance`
3. **Non-sealed interfaces/abstract classes** -> KSP processor generates mock implementations, `makeValueOf` uses `createUninitializedInstance` on those
4. **Unknown types** -> clear error message

## Scope

- Native only (JS/WasmJS not yet reported as broken)
- KSP processor changes to generate fakes for discovered interface/abstract parameter types
- Plugin changes to skip copying `MakeValueOf.kt` for `nativeMain` (KSP generates it instead)

## Findings

### Step 0 findings (verified)
- Upgraded Kotlin 2.3.0 -> 2.3.20, KSP 2.3.4 -> 2.3.7, AGP 8.12.3 -> 9.2.0, Gradle 9.2.1 -> 9.5.1
- AGP upgrade was required because KSP 2.3.7 + AGP 8.12.3 threw: "Kotlin multiplatform Variant API does not support addKspConfigurations() yet"
- AGP 9.2.0 requires Gradle 9.4.1+, so Gradle was also upgraded
- `androidLibrary` block is deprecated in AGP 9.x, should use `android` instead (warning only, not blocking)
- **Issue confirmed**: 7 of 45 tests fail with `ClassCastException` on iosSimulatorArm64:
  - `FileServiceTests` (5 failures) — uses `any()` matcher with non-primitive types
  - `SealedInterfaceServiceTests` (1 failure) — sealed interface valueOf
  - `ValueOfTests.makeValueOfReturnsOtherStuff` (1 failure) — direct makeValueOf test
- 38 tests pass — these use only primitive/collection types that go through the `when` block in `valueOf()`

## Steps

### Step 0: Verify the issue ✅
- [x] Upgrade Kotlin to 2.3.20, KSP to 2.3.7, AGP to 9.2.0, Gradle to 9.5.1
- [x] Run native tests (`iosSimulatorArm64Test`) and confirm the `ClassCastException`
- [x] Commit the version upgrade

### Step 1: Native `makeValueOf` with `createUninitializedInstance`
- [ ] Update `mockative-test/src/nativeMain/kotlin/io/mockative/fake/MakeValueOf.kt` to use `createUninitializedInstance` for concrete classes and sealed subclass walking
- [ ] The plugin bundles these sources automatically via `copySourcesToResources` task
- [ ] Run native tests to verify concrete class cases pass
- [ ] Commit

### Step 2: KSP processor — discover interface/abstract parameter types
- [ ] In the KSP processor, scan all function parameter types and property types across `@Mockable` types
- [ ] Identify non-sealed interfaces and abstract classes that need generated fakes
- [ ] Collect these types for generation
- [ ] Commit

### Step 3: KSP processor — generate fake implementations
- [ ] Generate minimal mock/fake classes for discovered interfaces and abstract classes
- [ ] Each fake implements all abstract members with `valueOf()` return values
- [ ] Commit

### Step 4: Wire generated fakes into `makeValueOf`
- [ ] Modify the plugin to skip copying `MakeValueOf.kt` for `nativeMain`
- [ ] Have the KSP processor generate the `actual fun makeValueOf` for native with:
  - `when` block mapping known interface/abstract types to `createUninitializedInstance<GeneratedFake>()`
  - Sealed subclass walking
  - `createUninitializedInstance` fallback for concrete classes
  - Error for unknown types
- [ ] Run native tests end-to-end
- [ ] Commit

### Step 5: Verify
- [ ] Run full test suite (`allTests` or at least JVM + native)
- [ ] Verify no regressions on JVM
- [ ] Commit any final fixes

## Version Changes

| Dependency | Old | New |
|---|---|---|
| Kotlin | 2.3.0 | 2.3.20 |
| KSP | 2.3.4 | 2.3.7 |
| AGP | 8.12.3 | 9.2.0 |
| Gradle | 9.2.1 | 9.5.1 |

## Key Files

- `mockative-test/src/nativeMain/kotlin/io/mockative/fake/MakeValueOf.kt` — native actual
- `mockative-test/src/commonMain/kotlin/io/mockative/fake/ValueOf.kt` — common valueOf
- `mockative-test/src/commonMain/kotlin/io/mockative/Matchers.kt` — matchers calling valueOf
- `mockative-processor/src/main/kotlin/io/mockative/MockativeSymbolProcessor.kt` — KSP entry point
- `mockative-processor/src/main/kotlin/io/mockative/ProcessableType.kt` — type processing
- `mockative-processor/src/main/kotlin/io/mockative/kotlinpoet/ClassName.Mockative.kt` — valueOf code gen
- `mockative-plugin/src/main/kotlin/io/mockative/MockativePlugin.kt` — plugin config
- `mockative-plugin/src/main/kotlin/io/mockative/MockativeProcessRuntimeTask.kt` — source copying
