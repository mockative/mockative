package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName

internal fun ClassName.parameterizedByAny(typeArguments: List<TypeVariableName>): TypeName {
    return if (typeArguments.isEmpty()) this else parameterizedBy(typeArguments)
}

internal fun ClassName.withTypeArguments(typeArguments: List<TypeName>): TypeName {
    return if (typeArguments.isEmpty()) this else parameterizedBy(typeArguments)
}

internal val ClassName.fullSimpleName: String
    get() = simpleNames.joinToString(".")
