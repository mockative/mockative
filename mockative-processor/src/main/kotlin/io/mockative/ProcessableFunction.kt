package io.mockative

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import io.mockative.kotlinpoet.toTypeNameMockative

@OptIn(KotlinPoetKspPreview::class)
data class ProcessableFunction(
    val declaration: KSFunctionDeclaration,
    val name: String,
    val returnType: TypeName,
    val isSuspend: Boolean,
    val typeVariables: List<TypeVariableName>,
    val typeParameterResolver: TypeParameterResolver,
    var parent: ProcessableType? = null,
) {
    companion object {
        @OptIn(KotlinPoetKspPreview::class)
        fun fromDeclaration(
            declaration: KSFunctionDeclaration,
            parentTypeParameterResolver: TypeParameterResolver
        ): ProcessableFunction {
            val typeParameterResolver = declaration.typeParameters
                .toTypeParameterResolver(parentTypeParameterResolver)

            return ProcessableFunction(
                declaration = declaration,
                name = declaration.simpleName.asString(),
                returnType = declaration.returnType!!.toTypeNameMockative(typeParameterResolver),
                isSuspend = declaration.modifiers.contains(Modifier.SUSPEND),
                typeVariables = declaration.typeParameters
                    .map { it.toTypeVariableName(typeParameterResolver) },
                typeParameterResolver = typeParameterResolver
            )
        }
    }
}