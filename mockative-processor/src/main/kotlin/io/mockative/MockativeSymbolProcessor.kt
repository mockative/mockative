package io.mockative

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import io.mockative.kotlinpoet.buildMockFunSpecs
import io.mockative.kotlinpoet.buildMockTypeSpec
import io.mockative.kotlinpoet.fullSimpleName
import java.io.File

class MockativeSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>,
    private val platform: MockativePlatform,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        println("[MockativeSymbolProcessor]")
        println("options: $options")
        println("platform: $platform")

        val tasks = options["io.mockative:tasks"]
        println("io.mockative:tasks=$tasks")

        if (tasks == null) {
            return emptyList()
        }

        val stubsUnitByDefault = options["mockative.stubsUnitByDefault"]
            ?.let { !it.equals("false", ignoreCase = true) }
            ?: true

        // Resolve the processable types
        val processableTypes = try {
            ProcessableType.fromResolver(resolver, stubsUnitByDefault)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }

        println("Found '${processableTypes.size}' processable types")

        // Generate Mock Classes
        processableTypes
            .forEach { type ->
                val packageName = type.mockClassName.packageName
                val fileName = type.mockClassName.fullSimpleName

                println("Processing '${type.mockClassName}'")

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

                val suppressDeprecation = AnnotationSpec.builder(SUPPRESS_ANNOTATION)
                    .addMember("%S", "DEPRECATION")
                    .addMember("%S", "DEPRECATION_ERROR")
                    .build()

                FileSpec.builder("io.mockative", fileName)
                    .addFunctions(type.buildMockFunSpecs())
                    .addAnnotation(suppressDeprecation)
                    .build()
                    .writeTo(codeGenerator, aggregating = false)
            }

        // Copy `mockative-test` library
//        try {
//            writeMockativeTest()
//        } catch (e: Throwable) {
//            e.printStackTrace()
//            throw e
//        }

        return emptyList()
    }

    private fun writeMockativeTest() {
        val directory = File("/Users/nicklas/git/Mockative/mockative/mockative-test/src/commonMain/kotlin")

        val platformSourceSet = when (platform) {
            MockativePlatform.JVM -> "jvmMain"
            MockativePlatform.JS -> "jsMain"
            MockativePlatform.WASM -> "wasmJsMain"
            MockativePlatform.NATIVE -> "nativeMain"
        }

        val platformDirectory = File("/Users/nicklas/git/Mockative/mockative/mockative-test/src/$platformSourceSet/kotlin")

        val kotlinDir = codeGenerator::class.java.declaredFields
            .first { it.name == "kotlinDir" }
            .also { it.isAccessible = true }
            .get(codeGenerator) as File

        if (kotlinDir.parentFile.name.endsWith("Test")) {
            return
        }

        directory.walkTopDown()
            .filter { it.isFile }
            .map { file ->
                val path = file.toRelativeString(directory)
                if (file.name == "MakeValueOf.kt" || file.name == "AtomicReference.kt") {
                    Triple(path, File(platformDirectory, path), true)
                } else {
                    Triple(path, file, true)
                }
            }
            .forEach { (path, file, replace) ->
                println("Processing '$path'")
                try {
                    codeGenerator.createNewFileByPath(Dependencies(false), path).use { os ->
                        var content = file.readText()

                        if (replace) {
                            content = content
                                .replace(" actual class", " class")
                                .replace(" actual constructor", " constructor")
                                .replace("actual var", "var ")
                                .replace("actual fun ", "fun ")
                        }

                        os.bufferedWriter().use { it.write(content) }
                    }
                } catch (e: FileAlreadyExistsException) {
                    // Nothing
                }
            }
    }
}
