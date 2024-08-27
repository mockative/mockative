package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.CodeBlock

data class CodeBlockWithMockInformation(val codeBlock: CodeBlock, val isMock: Boolean = false)