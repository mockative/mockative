package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.mockative.*

internal fun ClassName.parameterizedByAny(typeArguments: List<TypeVariableName>): TypeName {
    return if (typeArguments.isEmpty()) this else parameterizedBy(typeArguments)
}

internal fun ClassName.withTypeArguments(typeArguments: List<TypeName>): TypeName {
    return if (typeArguments.isEmpty()) this else parameterizedBy(typeArguments)
}

internal val ClassName.fullSimpleName: String
    get() = simpleNames.joinToString(".")

private fun isKotlinPackage(packageName: String): Boolean {
    return packageName == "kotlin" || packageName.startsWith("kotlin.")
}

private fun mockValue(type: ClassName): CodeBlock {
    val simpleName = type.simpleNames.joinToString("_")
    // Not allowed to use the kotlin package as base when creating new classes
    val mockPackageNamePrefix = if (isKotlinPackage(type.packageName)) "io.mockative." else ""
    val mockName = ClassName(mockPackageNamePrefix + type.packageName, "${simpleName}Mock")
    return CodeBlock.of("%T()", mockName)
}

internal fun valueOf(type: ClassName): CodeBlockWithMockInformation {
    return when (type) {
        BOOLEAN -> CodeBlockWithMockInformation(CodeBlock.of("%L", false))
        BYTE -> CodeBlockWithMockInformation(CodeBlock.of("%L", 0))
        SHORT -> CodeBlockWithMockInformation(CodeBlock.of("%L", 0))
        CHAR -> CodeBlockWithMockInformation(CodeBlock.of("%L.toChar()", 0))
        INT -> CodeBlockWithMockInformation(CodeBlock.of("%L", 0))
        LONG -> CodeBlockWithMockInformation(CodeBlock.of("%L", 0))
        FLOAT -> CodeBlockWithMockInformation(CodeBlock.of("%L", 0))
        DOUBLE -> CodeBlockWithMockInformation(CodeBlock.of("%L", 0))

        BOOLEAN_ARRAY -> CodeBlockWithMockInformation(CodeBlock.of("%T(0)", BOOLEAN_ARRAY))
        BYTE_ARRAY -> CodeBlockWithMockInformation(CodeBlock.of("%T(0)", BYTE_ARRAY))
        SHORT_ARRAY -> CodeBlockWithMockInformation(CodeBlock.of("%T(0)", SHORT_ARRAY))
        CHAR_ARRAY -> CodeBlockWithMockInformation(CodeBlock.of("%T(0)", CHAR_ARRAY))
        INT_ARRAY -> CodeBlockWithMockInformation(CodeBlock.of("%T(0)", INT_ARRAY))
        LONG_ARRAY -> CodeBlockWithMockInformation(CodeBlock.of("%T(0)", LONG_ARRAY))
        FLOAT_ARRAY -> CodeBlockWithMockInformation(CodeBlock.of("%T(0)", FLOAT_ARRAY))
        DOUBLE_ARRAY -> CodeBlockWithMockInformation(CodeBlock.of("%T(0)", DOUBLE_ARRAY))
        STRING -> CodeBlockWithMockInformation(CodeBlock.of("\"\""))
        CHAR_SEQUENCE -> CodeBlockWithMockInformation(CodeBlock.of("\"\""))

        ARRAY_LIST -> CodeBlockWithMockInformation(CodeBlock.of("%T()", ARRAY_LIST))
        ARRAY_DEQUE -> CodeBlockWithMockInformation(CodeBlock.of("%T()", ARRAY_DEQUE))
        LINKED_HASH_MAP -> CodeBlockWithMockInformation(CodeBlock.of("%T()", LINKED_HASH_MAP))
        HASH_MAP -> CodeBlockWithMockInformation(CodeBlock.of("%T()", HASH_MAP))
        LINKED_HASH_SET -> CodeBlockWithMockInformation(CodeBlock.of("%T()", LINKED_HASH_SET))
        HASH_SET -> CodeBlockWithMockInformation(CodeBlock.of("%T()", HASH_SET))

        else -> CodeBlockWithMockInformation(mockValue(type), true)
    }
}
