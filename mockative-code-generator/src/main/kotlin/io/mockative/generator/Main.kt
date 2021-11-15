package io.mockative.generator

import java.nio.file.Paths

fun main(args: Array<String>) {
    val outputDir = args.getOrNull(0) ?: Paths.get("..", "mockative/build/generated/mockative-code-generator").toAbsolutePath().toString()
    println("Generating code in $outputDir")

    val generator = CodeGenerator(outputDir)
    generator.generateCode()
}