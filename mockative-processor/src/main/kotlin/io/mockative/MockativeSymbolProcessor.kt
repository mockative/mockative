package io.mockative

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import io.mockative.kotlinpoet.buildMockFunSpecs
import io.mockative.kotlinpoet.buildMockTypeSpec
import io.mockative.kotlinpoet.fullSimpleName

class MockativeSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Resolve the processable types
        val processableTypes = try {
            log.info("options: $options")

            val configuration = MockativeConfiguration.fromOptions(options)
            log.info("configuration: $configuration")

            if (configuration.tasks.isEmpty()) {
                log.info("No test tasks detected. No mock code is generated.")
                return emptyList()
            }

            ProcessableType.fromResolver(configuration, resolver)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }

        log.info("Found '${processableTypes.size}' processable types")

        // Generate Mock Classes
        processableTypes
            .forEach { type ->
                val sourceClassName = type.sourceClassName
                val mockClassName = type.mockClassName

                val packageName = mockClassName.packageName
                val fileName = mockClassName.fullSimpleName

                log.info("Generating mock class '$mockClassName' for '$sourceClassName'")

                val file = FileSpec.builder(packageName, fileName)
                    .addType(type.buildMockTypeSpec())
                    .build()

                log.info("  Writing mock class '$mockClassName' to '${file.relativePath}'")

                file.writeTo(codeGenerator, aggregating = false)
            }

        // Generate Mock Functions
        processableTypes
            .forEach { type ->
                val mockClassName = type.mockClassName

                val reflectionName = type.sourceClassName.reflectionName()
                val fileName = "${reflectionName}.Mockative"

                log.info("Generating function for '$mockClassName'")

                val suppressDeprecation = AnnotationSpec.builder(SUPPRESS_ANNOTATION)
                    .addMember("%S", "DEPRECATION")
                    .addMember("%S", "DEPRECATION_ERROR")
                    .build()

                val file = FileSpec.builder("io.mockative", fileName)
                    .addFunctions(type.buildMockFunSpecs())
                    .addAnnotation(suppressDeprecation)
                    .build()

                log.info("  Writing function for '$mockClassName' to '${file.relativePath}'")

                file.writeTo(codeGenerator, aggregating = false)
            }

        return emptyList()
    }
}
