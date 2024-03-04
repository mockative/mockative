package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*

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