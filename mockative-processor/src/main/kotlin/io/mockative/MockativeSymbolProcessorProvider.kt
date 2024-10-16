package io.mockative

import com.google.devtools.ksp.processing.JsPlatformInfo
import com.google.devtools.ksp.processing.JvmPlatformInfo
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.NativePlatformInfo
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

lateinit var log: KSPLogger
lateinit var options: Map<String, String>

class MockativeSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        log = environment.logger
        options = environment.options

        return MockativeSymbolProcessor(environment.codeGenerator, environment.options, environment.platform)
    }

    private val SymbolProcessorEnvironment.platform: MockativePlatform
        get() {
            if (platforms.isEmpty()) {
                error("Could not resolve platform: No platform specified")
            }

            if (platforms.size > 1) {
                error("Could not resolve platform: More than one platform is specified: $platforms")
            }

            return when (val platform = platforms[0]) {
                is JvmPlatformInfo -> MockativePlatform.JVM
                is JsPlatformInfo -> MockativePlatform.JS
                is NativePlatformInfo -> MockativePlatform.NATIVE
                else -> when {
                    platform.platformName.startsWith("wasm-js") -> MockativePlatform.WASM
                    else -> error("Could not resolve platform: Unknown platform '${platform.platformName}' of type '${platform::class}'")
                }
            }
        }
}
