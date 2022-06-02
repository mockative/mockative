package io.mockative.kotlinpoet

import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.*
import com.squareup.kotlinpoet.tags.TypeAliasTag

internal fun TypeName.rawType(): ClassName {
    return findRawType() ?: throw IllegalArgumentException("Cannot get raw type from $this")
}

internal fun TypeName.findRawType(): ClassName? {
    return when (this) {
        is ClassName -> this
        is ParameterizedTypeName -> rawType
        is LambdaTypeName -> {
            var count = parameters.size
            if (receiver != null) {
                count++
            }
            val functionSimpleName = if (count >= 23) {
                "FunctionN"
            } else {
                "Function$count"
            }
            ClassName("kotlin.jvm.functions", functionSimpleName)
        }
        else -> null
    }
}

internal fun KSDeclaration.toClassNameInternal(): ClassName {
    require(!isLocal()) {
        "Local/anonymous classes are not supported!"
    }
    val pkgName = packageName.asString()
    val typesString = checkNotNull(qualifiedName).asString().removePrefix("$pkgName.")

    val simpleNames = typesString
        .split(".")
    return ClassName(pkgName, simpleNames)
}

@KotlinPoetKspPreview
internal fun KSType.toTypeNameMockative(
    typeParamResolver: TypeParameterResolver,
    typeArguments: List<KSTypeArgument>,
): TypeName {
    val type = when (val decl = declaration) {
        is KSClassDeclaration -> {
            decl.toClassName().withTypeArguments(arguments.map { it.toTypeName(typeParamResolver) })
        }
        is KSTypeParameter -> typeParamResolver[decl.name.getShortName()]
        is KSTypeAlias -> {
            val extraResolver = if (decl.typeParameters.isEmpty()) {
                typeParamResolver
            } else {
                decl.typeParameters.toTypeParameterResolver(typeParamResolver)
            }
            val mappedArgs = arguments.map { it.toTypeName(typeParamResolver) }

            val abbreviatedType = decl.type.resolve()
                .toTypeName(extraResolver)
                .copy(nullable = isMarkedNullable)
                .rawType()
                .withTypeArguments(mappedArgs)

            val aliasArgs = typeArguments.map { it.toTypeName(typeParamResolver) }

            decl.toClassNameInternal()
                .withTypeArguments(aliasArgs)
                .copy(tags = mapOf(TypeAliasTag::class to TypeAliasTag(abbreviatedType)))
        }
        else -> error("Unsupported type: $declaration")
    }

    return type.copy(nullable = isMarkedNullable)
}

/**
 * Returns a [TypeName] representation of this [KSTypeReference].
 *
 * @see toTypeParameterResolver
 * @param typeParamResolver an optional resolver for enclosing declarations' type parameters. Parent
 *                          declarations can be anything with generics that child nodes declare as
 *                          defined by [KSType.arguments].
 */
@KotlinPoetKspPreview
fun KSTypeReference.toTypeNameMockative(
    typeParamResolver: TypeParameterResolver = TypeParameterResolver.EMPTY
): TypeName {
    val resolved = resolve()
    return resolved.toTypeNameMockative(typeParamResolver, resolved.arguments)
}