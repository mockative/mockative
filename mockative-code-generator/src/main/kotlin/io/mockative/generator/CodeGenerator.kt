package io.mockative.generator

import java.io.File

fun main(args: Array<String>) {
    val buildDir = args.getOrNull(0) ?: "/Users/nicklas/git/mockative/mockative/build"

    val generatedDir = File(buildDir, "generated/mockative-code-generator/io/mockative")

    val program = Program(generatedDir)
    program.run()
}

