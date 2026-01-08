# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Mockative is a mocking framework for Kotlin/Native and Kotlin Multiplatform that uses the Kotlin Symbol Processing API (KSP). The project generates mock implementations of classes and interfaces annotated with `@Mockable` through compile-time code generation.

## Architecture

The project consists of four main modules:

### 1. mockative (Runtime Library)
- Location: `mockative/src/commonMain/kotlin`
- The core runtime library that end users depend on
- Provides the API for creating mocks, stubbing, and verification (`mock()`, `every`, `coEvery`, `verify`, `coVerify`)
- Contains the `@Mockable` annotation
- Targets all Kotlin Multiplatform platforms (JVM, JS, Native, Wasm)

### 2. mockative-processor (KSP Processor)
- Location: `mockative-processor/src/main/kotlin`
- JVM-only module that runs during compilation via KSP
- Implements `MockativeSymbolProcessorProvider` which creates `MockativeSymbolProcessor`
- Analyzes types annotated with `@Mockable` and generates mock implementations
- Uses KotlinPoet for code generation
- Key files:
  - `MockativeSymbolProcessorProvider.kt`: Entry point for KSP
  - `ProcessableType.kt` and `ProcessableFunction.kt`: Model the types/functions to mock
  - `kotlinpoet/` directory: Code generation logic using KotlinPoet

### 3. mockative-plugin (Gradle Plugin)
- Location: `mockative-plugin/src/main/kotlin`
- Gradle plugin that simplifies project setup
- Automatically applies KSP and kotlin-allopen plugins
- Configures source sets to include generated code
- Adds mockative runtime and processor dependencies
- Key file: `MockativePlugin.kt`
- The plugin copies test sources from `mockative-test/src` to its resources (see `copySourcesToResources` task)

### 4. mockative-test (Integration Tests)
- Location: `mockative-test/src/`
- Contains real-world test cases using Mockative
- Source sets for different platforms: `commonMain`, `jvmMain`, `androidMain`, `jsMain`, `nativeMain`, `wasmJsMain`, `wasmWasiMain`
- These sources are embedded into the plugin as resources for runtime dependency management

### 5. shared (Sample/Test Project)
- Location: `shared/src/`
- Example project demonstrating Mockative usage
- Applies the `io.mockative` plugin
- Used for testing the full integration

## Build Commands

### Building the project
```bash
./gradlew build
```

### Running tests

#### Run all tests (creates aggregated report)
```bash
./gradlew allTests
```

#### Run tests for specific platforms
```bash
./gradlew jvmTest              # JVM tests
./gradlew jsTest               # JavaScript tests
./gradlew iosSimulatorArm64Test # iOS Simulator tests
./gradlew macosArm64Test       # macOS tests
./gradlew linuxX64Test         # Linux tests
```

#### Run tests for a specific module
```bash
./gradlew :mockative:jvmTest
./gradlew :shared:jvmTest
```

### Publishing

#### Publish to Maven Local (for local testing)
```bash
./publish-local
# Or manually:
./gradlew publishToMavenLocal -Pmockative.projects=':mockative-plugin,:mockative,:mockative-processor'
```

#### Publish to Maven Central
```bash
./publish-central
# Or manually:
./gradlew publishAllPublicationsToMavenCentralRepository -Pmockative.projects=':mockative-plugin,:mockative,:mockative-processor'
```

### Cleaning
```bash
./gradlew clean
```

## Development Workflow

### Modifying the Runtime API
1. Edit code in `mockative/src/commonMain/kotlin`
2. Run `./gradlew :mockative:build` to compile
3. Test changes in `shared/` or `mockative-test/`

### Modifying the KSP Processor
1. Edit code in `mockative-processor/src/main/kotlin`
2. Run `./publish-local` to publish changes to Maven Local
3. Test in `shared/` which depends on the local version
4. The processor runs during compilation when types are annotated with `@Mockable`

### Modifying the Gradle Plugin
1. Edit code in `mockative-plugin/src/main/kotlin`
2. Run `./publish-local` to publish changes
3. Test in `shared/` or external projects

### Testing Changes End-to-End
1. Publish all modules locally: `./publish-local`
2. Use the `shared/` module to test the full integration
3. Ensure `shared/build.gradle.kts` uses `mavenLocal()` (already configured)

## Key Configuration Files

### gradle.properties
- `ksp.useKSP2=true`: Uses KSP2 (required for Kotlin 2.0+)
- `project.version=3.0.1`: Current version
- `project.group=io.mockative`: Maven coordinates

### settings.gradle.kts
- Conditional project inclusion via `mockative.projects` property
- Used by publish scripts to only publish specific modules

### buildSrc/
Contains convention plugins for consistent configuration:
- `convention.multiplatform.gradle.kts`: Configures all KMP targets (JVM, JS, Native, Wasm, Android)
- `convention.publication.gradle.kts`: Configures Maven publishing and signing

## Important Notes

### KSP and Code Generation
- Mockative generates mock implementations at compile time
- Generated code appears in `build/generated/ksp/` directories
- The plugin adds these directories as source sets automatically
- KSP must be configured with `ksp.useKSP2=true` in gradle.properties

### All-Open Plugin
- The Gradle plugin automatically applies `kotlin-allopen` to the `@Mockable` annotation
- This allows mocking of Kotlin classes (which are final by default)
- Configuration is in `MockativePlugin.kt:28-29`

### Platform-Specific Dependencies
- JVM/Android targets use additional runtime dependencies (Objenesis, Javassist) for advanced mocking
- See `mockative-test/build.gradle.kts` for platform-specific dependency configuration

### Testing Quirks
- Tests may fail with "KtInvalidLifetimeOwnerAccessException" - fix by setting `ksp.incremental=false` in gradle.properties
- The plugin automatically detects test tasks and adds required dependencies

## Version Information

- Current version: 3.0.1
- Requires Kotlin: 2.3.0
- Requires KSP: 2.3.0
- Supports Android compileSdk: 33
- Minimum Android SDK: 21
- JVM toolchain: 11
