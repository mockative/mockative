package io.mockative

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo
import io.mockative.ksp.addMock

@OptIn(KotlinPoetKspPreview::class)
class MockativeSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    options: Map<String, String>
) : SymbolProcessor {

    private val stubsUnitByDefault: Boolean = options["mockative.stubsUnitByDefault"].toBoolean()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val processableTypes = ProcessableType.fromResolver(resolver, stubsUnitByDefault)

        // Generate Mock classes
        processableTypes
            .forEach { type ->
                val packageName = type.sourceClassName.packageName
                val dotDelimitedSimpleName = type.sourceClassName.simpleNames.joinToString(".")

                FileSpec.builder(packageName, "${dotDelimitedSimpleName}.Mockative")
                    .addMock(type)
                    .build()
                    .writeTo(codeGenerator, aggregating = true)
            }

        return emptyList()
    }
}
