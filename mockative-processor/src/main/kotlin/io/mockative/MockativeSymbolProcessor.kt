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

        // Generate Mock Classes
        processableTypes
            .forEach { type ->
                val packageName = type.mockClassName.packageName
                val fileName = type.mockClassName.fullSimpleName

                FileSpec.builder(packageName, fileName)
                    .addType(type.buildMockTypeSpec())
                    .build()
                    .writeTo(codeGenerator, aggregating = false)
            }

        // Generate Mock Functions
        processableTypes
            .forEach { type ->
                val reflectionName = type.sourceClassName.reflectionName()
                val fileName = "${reflectionName}.Mockative"

                FileSpec.builder("io.mockative", fileName)
                    .addFunction(type.buildMockFunSpec())
                    .build()
                    .writeTo(codeGenerator, aggregating = false)
            }

        return emptyList()
    }
}
