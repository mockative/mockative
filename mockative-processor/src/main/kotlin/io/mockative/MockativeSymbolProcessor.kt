package io.mockative

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import io.mockative.kotlinpoet.buildMockFunSpec
import io.mockative.kotlinpoet.buildMockTypeSpec
import io.mockative.kotlinpoet.fullSimpleName

class MockativeSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val stubsUnitByDefault = options["mockative.stubsUnitByDefault"]
            ?.let { !it.equals("false", ignoreCase = true) }
            ?: true

        // Resolve the processable types
        val processableTypes = ProcessableType.fromResolver(resolver, stubsUnitByDefault)

        // Generate Mock classes
        processableTypes
            .forEach { type ->
                val packageName = type.mockClassName.packageName
                val fullSimpleName = type.mockClassName.fullSimpleName

                FileSpec.builder(packageName, "${fullSimpleName}.Mockative")
                    .addType(type.buildMockTypeSpec())
                    .build()
                    .writeTo(codeGenerator, aggregating = false)
            }

        // Generate Mock Functions
        // This must be generated in a separate file for some reason I have yet to discover...
        processableTypes
            .forEach { type ->
                val fullSimpleName = type.sourceClassName.fullSimpleName

                FileSpec.builder("io.mockative", "${fullSimpleName}.mock.Mockative")
                    .addFunction(type.buildMockFunSpec())
                    .build()
                    .writeTo(codeGenerator, aggregating = false)
            }

        return emptyList()
    }
}
