package io.mockative

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo
import io.mockative.ksp.addMocks
import java.io.OutputStreamWriter

@OptIn(KotlinPoetKspPreview::class)
class MockativeSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    options: Map<String, String>
) : SymbolProcessor {

    private var processed = false

    private val isDebugLogEnabled: Boolean = options["mockative.logging"]?.lowercase() == "debug"
    private val isInfoLogEnabled: Boolean = isDebugLogEnabled || options["mockative.logging"]?.lowercase() == "info"
    private val stubsUnitByDefault: Boolean = options["mockative.stubsUnitByDefault"].toBoolean()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        debug("Starting")

        if (processed) {
            debug("Skipped: Already Processed")
            return emptyList()
        }

        ProcessableFile.fromResolver(resolver)
            .forEach { file ->
                FileSpec.builder(file.packageName, "${file.fileName.removeSuffix(".kt")}.Mockative")
                    .addMocks(file.types)
                    .build()
                    .writeTo(codeGenerator, aggregating = false)
            }

        return emptyList()

        val annotatedSymbols = resolver.getSymbolsWithAnnotation(Mock::class.qualifiedName!!).toList()
        if (annotatedSymbols.isEmpty()) {
            debug("Skipped: No annotated symbols returned")
            return emptyList()
        }

        debug("Processing")

        val mocks = mutableListOf<KSClassDeclaration>()

        annotatedSymbols
            .mapNotNull { symbol -> symbol as? KSPropertyDeclaration }
            .mapNotNull { property ->
                (property.type.resolve().declaration as? KSClassDeclaration)
                    ?.let { it to property.containingFile }
            }
            .filter { (classDec, _) -> classDec.classKind == ClassKind.INTERFACE }
            .groupBy({ (classDec, _) -> classDec }, { (_, file) -> file })
            .forEach { (classDec, files) ->
                val mock = createMockDescriptor(classDec)

                val sources = (listOf(classDec.containingFile) + files)
                    .filterNotNull()
                    .toTypedArray()

                debug("Creating mock for ${classDec.qualifiedName?.asString()}")
                debug("  Usages:")
                files.filterNotNull().forEach { file ->
                    debug("    ${file.filePath}")
                }

                val dependencies = Dependencies(true, *sources)
                val os = codeGenerator.createNewFile(dependencies, mock.packageName, mock.mockName)
                val writer = OutputStreamWriter(os)
                val mockWriter = MockWriter(writer, stubsUnitByDefault)
                mockWriter.appendMock(mock)
                writer.flush()

                mocks.add(classDec)
            }

        if (mocks.isNotEmpty()) {
            debug("Writing GeneratedMocks.kt file")

            // Create mock(KClass) functions
            val sources = resolver.getAllFiles().toList().toTypedArray()
            val mocksFile = codeGenerator.createNewFile(Dependencies(false, *sources), "io.mockative", "GeneratedMocks")
            val mocksWriter = OutputStreamWriter(mocksFile)
            mocksWriter.appendLine("package io.mockative")
            mocksWriter.appendLine()

            mocks.forEach { mock ->
                // TODO Extract GeneratedMocks.kt generation into separate file
                val className = mock.qualifiedName!!.asString()

                val typeParameterBounds = if (mock.typeParameters.isNotEmpty()) {
                    mock.typeParameters
                        .flatMap { typeParam ->
                            typeParam.bounds
                                .map { bound -> "where ${typeParam.name.asString()} : ${bound.resolveUsageSyntax()}" }
                        }
                        .joinToString(" ")
                } else {
                    ""
                }

                val typeParameterList = if (mock.typeParameters.isNotEmpty()) {
                    "<${mock.typeParameters.joinToString(", "){ it.name.asString() }}>"
                } else {
                    ""
                }

                val kClassName = "$className$typeParameterList"
                val typeName = "$className$typeParameterList"
                val mockName = "${className}Mock$typeParameterList"

                mocksWriter.appendLine("internal fun${if (typeParameterList.isEmpty()) "" else " $typeParameterList"} mock(@Suppress(\"UNUSED_PARAMETER\") type: kotlin.reflect.KClass<$kClassName>): $typeName${if (typeParameterBounds.isEmpty()) "" else " $typeParameterBounds"} = $mockName()")
            }

            mocksWriter.flush()

            debug("${mocks.count()} mocks written to GeneratedMocks.kt file")

            info("Finished generating ${mocks.count()} mocks")
        }

        processed = true

        return emptyList()
    }

    private fun info(message: String) {
        if (isInfoLogEnabled) {
            logger.info("[Mockative] $message")
        }
    }

    private fun debug(message: String) {
        if (isDebugLogEnabled) {
            logger.info("[Mockative] $message")
        }
    }
}
