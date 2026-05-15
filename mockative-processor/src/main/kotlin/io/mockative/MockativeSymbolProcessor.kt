package io.mockative

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.NativePlatformInfo
import com.google.devtools.ksp.processing.PlatformInfo
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import io.mockative.kotlinpoet.AnnotationAggregator
import io.mockative.kotlinpoet.buildMockFunSpecs
import io.mockative.kotlinpoet.buildMockTypeSpec
import io.mockative.kotlinpoet.fullSimpleName

class MockativeSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>,
    private val platforms: List<PlatformInfo>,
) : SymbolProcessor {
    private var hasGeneratedNativeMakeValueOf = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            log.info("options: $options")

            val configuration = MockativeConfiguration.fromOptions(options)
            log.info("configuration: $configuration")

            if (configuration.disabled) {
                log.info("Code generation is disabled due to plugin being disabled")
                return emptyList()
            }

            // Resolve the processable types
            val processableTypes = ProcessableType.fromResolver(configuration, resolver)
                .filter { type ->
                    when (type.sourceClassName.toString()) {
                        "kotlin.collections.MutableList" -> false
                        else -> true
                    }
                }

            log.info("Found '${processableTypes.size}' processable types")

            // Generate Mock Classes
            processableTypes.forEach { type ->
                val sourceClassName = type.sourceClassName.toString()
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
            processableTypes.forEach { type ->
                val mockClassName = type.mockClassName

                val reflectionName = type.sourceClassName.reflectionName()
                val fileName = "${reflectionName}.Mockative"

                log.info("Generating function for '$mockClassName'")

                val annotations = AnnotationAggregator()

                val file = FileSpec.builder(PackageResolver.Mockative.resolve(), fileName)
                    .addFunctions(type.buildMockFunSpecs())
                    .addAnnotations(annotations.build())
                    .build()

                log.info("  Writing function for '$mockClassName' to '${file.relativePath}'")

                file.writeTo(codeGenerator, aggregating = false)
            }

            // Generate native-specific makeValueOf (only for main source sets with @Mockable types)
            val isNative = platforms.any { it is NativePlatformInfo }
            log.info("platforms: ${platforms.map { it.platformName }}, isNative=$isNative, processableTypes=${processableTypes.size}")
            if (isNative && !hasGeneratedNativeMakeValueOf && processableTypes.isNotEmpty()) {
                log.info("Generating native makeValueOf for platform: ${platforms.map { it.platformName }}")
                val generator = NativeMakeValueOfGenerator(codeGenerator, configuration, processableTypes)
                generator.generate()
                hasGeneratedNativeMakeValueOf = true
            }

            return emptyList()
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }
}
