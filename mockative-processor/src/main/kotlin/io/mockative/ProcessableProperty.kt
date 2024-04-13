package io.mockative

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import io.mockative.kotlinpoet.toTypeNameMockative

data class ProcessableProperty(
    val declaration: KSPropertyDeclaration,
    val name: String,
    val type: TypeName,
    val typeParameterResolver: TypeParameterResolver,
    val receiver: TypeName?,
) {
    companion object {
        fun fromDeclaration(
            declaration: KSPropertyDeclaration,
            parentTypeParameterResolver: TypeParameterResolver
        ): ProcessableProperty {
            val typeParameterResolver = declaration.typeParameters
                .toTypeParameterResolver(parentTypeParameterResolver)

            return ProcessableProperty(
                declaration = declaration,
                name = declaration.simpleName.asString(),
                type = declaration.type.toTypeNameMockative(typeParameterResolver),
                typeParameterResolver = typeParameterResolver,
                receiver = declaration.extensionReceiver?.toTypeNameMockative(typeParameterResolver),
            )
        }
    }
}
