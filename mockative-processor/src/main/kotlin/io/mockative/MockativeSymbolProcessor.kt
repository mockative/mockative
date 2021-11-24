package io.mockative

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.OutputStreamWriter

class MockativeSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    options: Map<String, String>
) : SymbolProcessor {

    private var processed = false

    private val isDebugLogEnabled: Boolean = options["mockative.logging"]?.lowercase() == "debug"
    private val isInfoLogEnabled: Boolean = isDebugLogEnabled || options["mockative.logging"]?.lowercase() == "info"

    override fun process(resolver: Resolver): List<KSAnnotated> {
        debug("Starting")

        if (processed) {
            return emptyList()
        }

        debug("Processing")

        val mocks = mutableListOf<KSClassDeclaration>()

        resolver.getSymbolsWithAnnotation(Mock::class.qualifiedName!!)
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
                val mockWriter = MockWriter(writer)
                mockWriter.appendMock(mock)
                writer.flush()

                mocks.add(classDec)
            }

        if (mocks.isNotEmpty()) {
            debug("Writing GeneratedMocks.kt file")

            // Create mock(KClass) functions
            val sources = mocks.mapNotNull { it.containingFile }.toTypedArray()
            val mocksFile = codeGenerator.createNewFile(Dependencies(true, *sources), "io.mockative", "GeneratedMocks")
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
