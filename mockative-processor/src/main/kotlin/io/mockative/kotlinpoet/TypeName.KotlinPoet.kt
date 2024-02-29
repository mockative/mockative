package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*
import io.mockative.ARRAY_DEQUE
import io.mockative.ARRAY_LIST
import io.mockative.HASH_MAP
import io.mockative.HASH_SET
import io.mockative.LINKED_HASH_MAP
import io.mockative.LINKED_HASH_SET

internal fun TypeName.applySafeAnnotations(): TypeName {
    return when (this) {
        is ParameterizedTypeName -> this.applySafeAnnotations()
        is TypeVariableName -> this.applySafeAnnotations()
        else -> this
    }
}

private fun ParameterizedTypeName.applySafeAnnotations(): TypeName {
    val typeArgumentsWithAnnotations = this.typeArguments.map { typeArg ->
        if (typeArg is TypeVariableName) {
            typeArg.applySafeAnnotations()
        } else {
            typeArg
        }
    }
    return this.copy(typeArguments = typeArgumentsWithAnnotations)
}

private fun TypeVariableName.applySafeAnnotations(): TypeName {
    return this.addAnnotations(listOf(AnnotationSpec.builder(UnsafeVariance::class).build()))
}

private fun TypeName.addAnnotations(additionalAnnotations: List<AnnotationSpec>): TypeName {
    return copy(annotations = this.annotations + additionalAnnotations)
}

private fun isKotlinPackage(packageName: String): Boolean {
    return packageName == "kotlin" || packageName.startsWith("kotlin.")
}

private fun mockValue(type: ClassName): CodeBlock {
    val simpleName = type.simpleNames.joinToString("_")
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
