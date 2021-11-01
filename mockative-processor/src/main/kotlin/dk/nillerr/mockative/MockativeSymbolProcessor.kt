package dk.nillerr.mockative

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.OutputStreamWriter
import kotlin.reflect.KClass

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

        val mocksClassDecs = resolver.getSymbolsWithAnnotation(Mocks::class.qualifiedName!!)
            .mapNotNull { symbol -> symbol as? KSDeclaration }
            .flatMap { declaration ->
                declaration.annotations
                    .mapNotNull { annotation -> annotation.arguments.firstOrNull { it.name?.asString() == "value" } }
                    .mapNotNull { argument -> (argument.value as? KSType)?.declaration as? KSClassDeclaration }
                    .map { classDec -> classDec to declaration.containingFile }
            }

        val mockClassDecs = resolver.getSymbolsWithAnnotation(Mock::class.qualifiedName!!)
            .mapNotNull { symbol -> symbol as? KSPropertyDeclaration }
            .mapNotNull { property ->
                (property.type.resolve().declaration as? KSClassDeclaration)
                    ?.let { it to property.containingFile }
            }

        (mocksClassDecs + mockClassDecs)
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
            debug("Writing Mocks.kt file")

            // Create mock(KClass) functions
            val sources = mocks.mapNotNull { it.containingFile }.toTypedArray()
            val mocksFile = codeGenerator.createNewFile(Dependencies(true, *sources), "dk.nillerr.mockative", "Mocks")
            val mocksWriter = OutputStreamWriter(mocksFile)
            mocksWriter.appendLine("package dk.nillerr.mockative")
            mocksWriter.appendLine()

            mocks.forEach {
                val typeName = it.qualifiedName!!.asString()
                mocksWriter.appendLine("internal fun mock(@Suppress(\"UNUSED_PARAMETER\") type: kotlin.reflect.KClass<$typeName>): $typeName = ${typeName}Mock()")
            }

            mocksWriter.flush()

            debug("${mocks.count()} mocks written to Mocks.kt file")

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
