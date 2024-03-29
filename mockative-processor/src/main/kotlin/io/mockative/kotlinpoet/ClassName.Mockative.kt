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

internal fun valueOf(type: ClassName): CodeBlock {
    return when (type) {
        BOOLEAN -> CodeBlock.of("%L", false)
        BYTE -> CodeBlock.of("%L", 0)
        SHORT -> CodeBlock.of("%L", 0)
        CHAR -> CodeBlock.of("%L.toChar()", 0)
        INT -> CodeBlock.of("%L", 0)
        LONG -> CodeBlock.of("%L", 0)
        FLOAT -> CodeBlock.of("%L", 0)
        DOUBLE -> CodeBlock.of("%L", 0)

        BOOLEAN_ARRAY -> CodeBlock.of("%T(0)", BOOLEAN_ARRAY)
        BYTE_ARRAY -> CodeBlock.of("%T(0)", BYTE_ARRAY)
        SHORT_ARRAY -> CodeBlock.of("%T(0)", SHORT_ARRAY)
        CHAR_ARRAY -> CodeBlock.of("%T(0)", CHAR_ARRAY)
        INT_ARRAY -> CodeBlock.of("%T(0)", INT_ARRAY)
        LONG_ARRAY -> CodeBlock.of("%T(0)", LONG_ARRAY)
        FLOAT_ARRAY -> CodeBlock.of("%T(0)", FLOAT_ARRAY)
        DOUBLE_ARRAY -> CodeBlock.of("%T(0)", DOUBLE_ARRAY)
        STRING -> CodeBlock.of("\"\"")
        CHAR_SEQUENCE -> CodeBlock.of("\"\"")

        ARRAY_LIST -> CodeBlock.of("%T()", ARRAY_LIST)
        ARRAY_DEQUE -> CodeBlock.of("%T()", ARRAY_DEQUE)
        LINKED_HASH_MAP -> CodeBlock.of("%T()", LINKED_HASH_MAP)
        HASH_MAP -> CodeBlock.of("%T()", HASH_MAP)
        LINKED_HASH_SET -> CodeBlock.of("%T()", LINKED_HASH_SET)
        HASH_SET -> CodeBlock.of("%T()", HASH_SET)

        else -> mockValue(type)
    }
}
