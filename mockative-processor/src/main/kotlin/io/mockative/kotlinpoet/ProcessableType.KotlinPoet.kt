package io.mockative.kotlinpoet

import com.google.devtools.ksp.symbol.ClassKind
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.mockative.*
import io.mockative.ksp.addOriginatingKSFiles

internal fun ProcessableType.buildMockFunSpec(): FunSpec {
    val suppressUnusedParameter = AnnotationSpec.builder(SUPPRESS_ANNOTATION)
        .addMember("%S", "UNUSED_PARAMETER")
        .build()

    val parameterizedSourceClassName = sourceClassName.parameterizedByAny(typeVariables)
    val parameterizedMockClassName = mockClassName.parameterizedByAny(typeVariables)

    val typeType = KCLASS.parameterizedBy(parameterizedSourceClassName)

    val typeParameter = ParameterSpec.builder("type", typeType)
        .addAnnotation(suppressUnusedParameter)
        .build()
    val spyParameter = ParameterSpec.builder("spyInstance", parameterizedSourceClassName.copy(nullable = false))
        .build()

    val modifiers = buildList {
        if (declaration.isEffectivelyInternal()) {
            add(KModifier.INTERNAL)
        }
    }

    val functionTypeVariables = typeVariables.map { it.withoutVariance() }
    val functionName = if (isSpy) "spy" else "mock"

    val parameters = if (isSpy) {
        listOf(typeParameter, spyParameter)
    } else {
        listOf(typeParameter)
    }
    val addInitializer = if (isSpy) {
        "spyInstance"
    } else {
        "null"
    }

    return FunSpec.builder(functionName)
        .addModifiers(modifiers)
        .addTypeVariables(functionTypeVariables)
        .addParameters(parameters)
        .returns(parameterizedSourceClassName)
        .addStatement("return %M(%T(%L)) { stubsUnitByDefault=%L; isSpy=%L }", CONFIGURE, parameterizedMockClassName, addInitializer, stubsUnitByDefault, isSpy)
        .addOriginatingKSFiles(usages)
        .build()
}

private fun TypeVariableName.withoutVariance(): TypeVariableName {
    return TypeVariableName(name = name, bounds = bounds)
}

internal fun ProcessableType.buildMockTypeSpec(): TypeSpec {
    val spyInstanceName = "instance"
    val properties = buildPropertySpecs(spyInstanceName)
    val functions = buildFunSpecs(spyInstanceName)
    val constructorSpec = FunSpec.constructorBuilder()
        .addParameter(
            ParameterSpec.builder(spyInstanceName, sourceClassName.parameterizedByAny(typeVariables).copy(nullable = true))
                .defaultValue("null")
                .build()
        )
        .build()
    val instanceInitializer = PropertySpec.builder(
        spyInstanceName, sourceClassName.parameterizedByAny(typeVariables).copy(nullable = true)
    )
        .initializer("instance")
        .addModifiers(KModifier.PRIVATE)
        .build()

    val modifiers = buildList {
        if (declaration.isEffectivelyInternal()) {
            add(KModifier.INTERNAL)
        }
    }

    val typeSpec = TypeSpec.classBuilder(mockClassName)
        .addModifiers(modifiers)
        .addTypeVariables(typeVariables)

    if (declaration.classKind == ClassKind.CLASS) {
        typeSpec.superclass(sourceClassName.parameterizedByAny(typeVariables))

        constructorParameters
            .map { it.type.toTypeNameMockative(typeParameterResolver).copy(nullable = false).rawType() }
            .forEach { type ->
                typeSpec.addSuperclassConstructorParameter("%L", valueOf(type))
            }
    } else if (declaration.classKind == ClassKind.INTERFACE) {
        typeSpec.addSuperinterface(sourceClassName.parameterizedByAny(typeVariables))
    }

    return typeSpec
        .primaryConstructor(constructorSpec)
        .addProperties(properties.plus(instanceInitializer))
        .addFunctions(functions)
        .addKdoc(declaration.docString?.trim() ?: "")
        .addOriginatingKSFiles(usages)
        .build()
}

private fun ProcessableType.buildPropertySpecs(spyInstanceName: String): List<PropertySpec> {
    return properties
        .map { it.buildPropertySpec(spyInstanceName) }
        .toList()
}

private fun ProcessableType.buildFunSpecs(spyInstanceName: String): List<FunSpec> {
    return functions
        .map { it.buildFunSpec(spyInstanceName) }
        .toList()
}
