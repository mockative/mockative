# Plan: Fix Kotlin/Native `makeValueOf` for Kotlin 2.3.20

## Status: COMPLETE

## Problem

Kotlin 2.3.20 introduces real downcast checks at reified generic return boundaries on
Kotlin/Native. Mockative's `makeValueOf` on non-JVM platforms returns `Unit as T`, which
now throws `ClassCastException` instead of silently passing.

## Solution

KSP processor generates a native-specific `actual fun makeValueOf` at compile time with
`createUninitializedInstance` for each discovered type:

1. **Concrete classes** -> `createUninitializedInstance<ConcreteClass>()`
2. **Sealed types** -> KSP resolves to a concrete leaf, then `createUninitializedInstance`
3. **Non-sealed interfaces/abstract classes** -> KSP generates a fake implementation, then `createUninitializedInstance` on that
4. **Unknown types** -> `ValueCreationNotSupportedException`

## Steps Completed

- [x] Upgrade Kotlin 2.3.0 -> 2.3.20, KSP 2.3.4 -> 2.3.7, AGP 8.12.3 -> 9.2.0, Gradle 9.2.1 -> 9.5.1
- [x] Verify issue: 7/45 iOS tests fail with ClassCastException
- [x] Verify `createUninitializedInstance` works on Native (reified-only, requires opt-in annotations)
- [x] Implement NativeMakeValueOfGenerator in KSP processor
- [x] Generate fake classes for interfaces/abstract classes
- [x] Generate `actual fun makeValueOf` with when-block for all discovered types
- [x] Plugin excludes native `MakeValueOf.kt` from resource copying
- [x] Handle multimodule package normalization
- [x] Handle test vs main source set deduplication
- [x] All 45 iOS tests pass
- [x] All JVM tests pass (no regressions)

## Version Changes

| Dependency | Old | New |
|---|---|---|
| Kotlin | 2.3.0 | 2.3.20 |
| KSP | 2.3.4 | 2.3.7 |
| AGP | 8.12.3 | 9.2.0 |
| Gradle | 9.2.1 | 9.5.1 |
