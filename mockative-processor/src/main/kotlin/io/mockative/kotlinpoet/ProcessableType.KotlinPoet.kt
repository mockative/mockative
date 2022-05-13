package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import io.mockative.*
import io.mockative.ksp.addOriginatingKSFiles
import io.mockative.ksp.getAllDependentFiles

@OptIn(KotlinPoetKspPreview::class)
internal fun ProcessableType.buildMockTypeSpec(): TypeSpec {
    val properties = buildPropertySpecs()
    val functions = buildFunSpecs()

    val dependentFiles = declaration.getAllDependentFiles()

    val parameterSpec = ParameterSpec.builder("stubsUnitByDefault", BOOLEAN)
        .build()

    val primaryConstructor = FunSpec.constructorBuilder()
        .addParameter(parameterSpec)
        .build()

    val typeParameterResolver = declaration.typeParameters.toTypeParameterResolver()

    val typeVariables = declaration.typeParameters
        .map { it.toTypeVariableName(typeParameterResolver) }

    return TypeSpec.classBuilder(mockClassName)
        .addTypeVariables(typeVariables)
        .primaryConstructor(primaryConstructor)
        .superclass(MOCKABLE)
        .addSuperclassConstructorParameter("%N", parameterSpec)
        .addSuperinterface(if (typeVariables.isEmpty()) sourceClassName else sourceClassName.parameterizedBy(typeVariables))
        .addProperties(properties)
        .addFunctions(functions)
        .addKdoc(declaration.docString?.trim() ?: "")
        .addOriginatingKSFiles(dependentFiles)
        .build()
}

internal fun ProcessableType.buildPropertySpecs(): List<PropertySpec> {
    return properties
        .map { it.buildPropertySpec() }
        .toList()
}

@OptIn(KotlinPoetKspPreview::class)
internal fun ProcessableProperty.buildPropertySpec(): PropertySpec {
    val modifiers = listOf(KModifier.OVERRIDE)
    val returnsUnit = if (type == UNIT) "true" else "false"

    return PropertySpec.builder(name, type, modifiers)
        .mutable(declaration.isMutable)
        .getter(
            FunSpec.getterBuilder()
                .addStatement("return %M<%T>(this, %T(%S), %L)", MOCKABLE_INVOKE, type, INVOCATION_GETTER, name, returnsUnit)
                .build()
        )
        .setter(
            if (declaration.isMutable) {
                val value = ParameterSpec.builder("value", type)
                    .build()

                FunSpec.setterBuilder()
                    .addParameter(value)
                    .addStatement("%M<%T>(this, %T(%S, %N), true)", MOCKABLE_INVOKE, type, INVOCATION_SETTER, name, value)
                    .build()
            } else null
        )
        .build()
}

internal fun ProcessableType.buildFunSpecs(): List<FunSpec> {
    return functions
        .map { it.buildFunSpec() }
        .toList()
}

@OptIn(KotlinPoetKspPreview::class)
internal fun ProcessableFunction.buildFunSpec(): FunSpec {
    val modifiers = buildList {
        add(KModifier.OVERRIDE)

        if (isSuspend) {
            add(KModifier.SUSPEND)
        }
    }

    val returnsUnit = if (returnType == UNIT) "true" else "false"

    val invocation = if (isSuspend) MOCKABLE_SUSPEND else MOCKABLE_INVOKE

    val argumentsListFormat = declaration.parameters.joinToString(", ") { "%L" }
    val arguments = declaration.parameters.map { it.name!!.asString() }

    val argumentsListValues = CodeBlock.builder()
        .add(argumentsListFormat, *arguments.toTypedArray())
        .build()

    val argumentsList = CodeBlock.builder()
        .add("%M<%T?>(%L)", LIST_OF, ANY, argumentsListValues)
        .build()

    return FunSpec.builder(name)
        .addModifiers(modifiers)
        .returns(returnType)
        .addParameters(
            declaration.parameters.map {
                ParameterSpec.builder(it.name!!.asString(), it.type.resolve().toTypeName(typeParameterResolver))
                    .build()
            }
        )
        .addTypeVariables(typeVariables)
        .addStatement("return %M<%T>(this, %T(%S, %L), %L)", invocation, returnType, INVOCATION_FUNCTION, name, argumentsList, returnsUnit)
        .build()
}
