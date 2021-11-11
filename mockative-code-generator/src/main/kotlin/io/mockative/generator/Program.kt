package io.mockative.generator

import java.io.File

class Program(private val generatedDir: File) {
    fun run() {
        generatedDir.deleteRecursively()
        generatedDir.mkdirs()

        generateFiles("WhenInvokingBuilder") { count -> "WhenInvoking${count}Builder.kt"}
        generateFiles("WhenSuspendingBuilder") { count -> "WhenSuspending${count}Builder.kt"}
    }

    private fun generateFiles(templateName: String, filename: (Int) -> String) {
        val template = readResourceSource(templateName)

        for (count in 3..9) {
            val tokens: Map<String, String> = createTokens(count)

            val output = tokens.entries
                .fold(template) { text, (key, value) -> text.replace("#$key#", value) }

            val file = File(generatedDir, filename(count))
            file.writeText(output)
        }
    }

    private fun generateFile(template: String, count: Int) {
        val tokens: Map<String, String> = createTokens(count)

        val output = tokens.entries
            .fold(template) { text, (key, value) -> text.replace("#$key#", value) }

        val file = File(generatedDir, "WhenInvoking${count}Builder.kt")
        file.writeText(output)
    }

    private fun createTokens(count: Int): Map<String, String> = mapOf(
        "type-param-list" to (1..count).joinToString(", ") { "P$it" },
        "count" to "$count",
        "with.parameters" to (1..count).joinToString(", ") { "p$it: Matcher<P$it>" },
        "with.arguments" to (1..count).joinToString(", ") { "p$it" },
        "then.arguments" to (1..count).joinToString(", ") { "args[${it - 1}] as P${it}" },
        "thenInvoke.underscores" to (1..count).joinToString(", ") { "_" }
    )
}