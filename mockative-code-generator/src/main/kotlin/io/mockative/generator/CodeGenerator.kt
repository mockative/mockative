package io.mockative.generator

import java.io.File

internal class CodeGenerator(outputPath: String) {
    private val outputDir = File(outputPath, "io/mockative")

    fun generateCode() {
        outputDir.deleteRecursively()
        outputDir.mkdirs()

//        generateFiles("GivenFunctionBuilder") { count -> "GivenFunction${count}Builder.kt"}
//        generateFiles("GivenSuspendFunctionBuilder") { count -> "GivenSuspendFunction${count}Builder.kt"}
//        generateFiles("VerifyFunctionBuilder") { count -> "VerifyFunction${count}Builder.kt"}
//
//        generateKFunctions()
//        generateGivenBuilder()
//        generateVerifyThatBuilder()
    }

    private fun generateKFunctions() {
        val template = readResourceSource("KFunction")

        val functions = StringBuilder()

        val functionTemplate = readResourceSource("KFunction_function")
        for (count in 1..9) {
            functions.appendLine(replaceTokens(count, functionTemplate))
        }

        val output = template.replace("#functions#", functions.toString())

        val file = File(outputDir, "KFunction.kt")
        file.writeText(output)
    }

    private fun generateVerifyThatBuilder() {
        val template = readResourceSource("VerifyThatBuilder")

        val functionTemplate = readResourceSource("VerifyThatBuilder_function")
        val suspendFunctionTemplate = readResourceSource("VerifyThatBuilder_suspendFunction")

        val functions = StringBuilder()
        val suspendFunctions = StringBuilder()
        for (count in 1..9) {
            functions.appendLine(replaceTokens(count, functionTemplate))
            suspendFunctions.appendLine(replaceTokens(count, suspendFunctionTemplate))
        }

        val output = template
            .replace("#functions#", functions.toString())
            .replace("#suspend-functions#", suspendFunctions.toString())

        val file = File(outputDir, "VerifyThatBuilder.kt")
        file.writeText(output)
    }

    private fun generateGivenBuilder() {
        val template = readResourceSource("GivenBuilder")

        val functionTemplate = readResourceSource("GivenBuilder_function")
        val suspendFunctionTemplate = readResourceSource("GivenBuilder_suspendFunction")

        val functions = StringBuilder()
        val suspendFunctions = StringBuilder()
        for (count in 1..9) {
            functions.appendLine(replaceTokens(count, functionTemplate))
            suspendFunctions.appendLine(replaceTokens(count, suspendFunctionTemplate))
        }

        val output = template
            .replace("#functions#", functions.toString())
            .replace("#suspend-functions#", suspendFunctions.toString())

        val file = File(outputDir, "GivenBuilder.kt")
        file.writeText(output)
    }

    private fun generateFiles(templateName: String, filename: (Int) -> String) {
        val template = readResourceSource(templateName)

        for (count in 1..9) {
            generateFile(template, count, filename(count))
        }
    }

    private fun generateFile(template: String, count: Int, filename: String) {
        val output = replaceTokens(count, template)
        val file = File(outputDir, filename)
        file.writeText(output)
    }

    private fun replaceTokens(count: Int, template: String): String {
        val tokens: Map<String, String> = createTokens(count)

        return tokens.entries
            .fold(template) { text, (key, value) -> text.replace("#$key#", value) }
    }

    private fun createTokens(count: Int): Map<String, String> {
        val typeParameterCount = 1..count

        return mapOf(
            "type-param-list" to typeParameterCount.joinToString(", ") { "P$it" },
            "reified-type-param-list" to typeParameterCount.joinToString(", ") { "reified P$it" },
            "count" to "$count",
            "with.parameters" to typeParameterCount.joinToString(", ") { "p$it: P$it = any()" },
            "with.arguments" to typeParameterCount.joinToString(", ") { "p$it" },
            "then.arguments" to typeParameterCount.joinToString(", ") { "args[${it - 1}] as P${it}" },
            "thenInvoke.underscores" to typeParameterCount.joinToString(", ") { "_" }
        )
    }
}
