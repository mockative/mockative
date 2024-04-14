package io.mockative

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

lateinit var log: KSPLogger
lateinit var options: Map<String, String>

class MockativeSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        log = environment.logger
        options = environment.options

        return MockativeSymbolProcessor(environment.codeGenerator, environment.options)
    }
}
