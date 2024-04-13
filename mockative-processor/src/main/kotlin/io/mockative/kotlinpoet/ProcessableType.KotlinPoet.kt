package io.mockative.kotlinpoet

import com.google.devtools.ksp.symbol.ClassKind
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.mockative.*
import io.mockative.ksp.addOriginatingKSFiles

internal fun ProcessableType.buildMockFunSpecs(): List<FunSpec> {
    val suppressUnusedParameter = AnnotationSpec.builder(SUPPRESS_ANNOTATION)
        .addMember("%S", "UNUSED_PARAMETER")
        .build()

    val parameterizedSourceClassName = sourceClassName.parameterizedByAny(typeVariables)
    val parameterizedMockClassName = mockClassName.parameterizedByAny(typeVariables)

    val typeType = KCLASS.parameterizedBy(parameterizedSourceClassName)

    val typeParameter = ParameterSpec.builder("type", typeType)
        .addAnnotation(suppressUnusedParameter)
        .build()
    val spyParameter = ParameterSpec.builder("on", parameterizedSourceClassName.copy(nullable = false))
        .build()

    val modifiers = buildList {
        if (declaration.isEffectivelyInternal()) {
            add(KModifier.INTERNAL)
        }
    }

    val functionTypeVariables = typeVariables.map { it.withoutVariance() }

    val mock = buildMockFunSpec(
        functionName = "mock",
        returnType = parameterizedSourceClassName,
        mockClassName = parameterizedMockClassName,
        typeParameter = typeParameter,
        modifiers = modifiers,
        functionTypeVariables = functionTypeVariables,
        stubsUnitByDefault = stubsUnitByDefault
    )

    val spy1 = buildMockFunSpec(
        functionName = "spy",
        returnType = parameterizedSourceClassName,
        mockClassName = parameterizedMockClassName,
        typeParameter = typeParameter,
        spyParameter = spyParameter,
        modifiers = modifiers,
        functionTypeVariables = functionTypeVariables,
        stubsUnitByDefault = stubsUnitByDefault
    )

    val spy2 = buildMockFunSpec(
        functionName = "spyOn",
        returnType = parameterizedSourceClassName,
        mockClassName = parameterizedMockClassName,
        spyParameter = spyParameter,
        modifiers = modifiers,
        functionTypeVariables = functionTypeVariables,
        stubsUnitByDefault = stubsUnitByDefault,
    )

    return listOf(mock, spy1, spy2)
}

internal fun ProcessableType.buildMockFunSpec(
    functionName: String,
    returnType: TypeName,
    mockClassName: TypeName,
    typeParameter: ParameterSpec? = null,
    spyParameter: ParameterSpec? = null,
    modifiers: List<KModifier>,
    functionTypeVariables: List<TypeVariableName>,
    stubsUnitByDefault: Boolean,
): FunSpec {
    val parameters = listOfNotNull(typeParameter, spyParameter)
    val addSpyInitializer = spyParameter?.name ?: "null"

    return FunSpec.builder(functionName)
        .addModifiers(modifiers)
        .addTypeVariables(functionTypeVariables)
        .addParameters(parameters)
        .returns(returnType)
        .addStatement("return %M(%T(%L)) { stubsUnitByDefault·=·%L }", CONFIGURE, mockClassName, addSpyInitializer, stubsUnitByDefault)
        .addOriginatingKSFiles(usages)
        .build()
}

private fun TypeVariableName.withoutVariance(): TypeVariableName {
    return TypeVariableName(name = name, bounds = bounds)
}

internal fun ProcessableType.buildMockTypeSpec(): TypeSpec {
    val properties = buildPropertySpecs()
    val functions = buildFunSpecs()

    val spyInstanceType = sourceClassName.parameterizedByAny(typeVariables).copy(nullable = true)
    val spyInstanceParam = ParameterSpec.builder(spyInstanceName, spyInstanceType)
        .defaultValue("null")
        .build()

    val constructorSpec = FunSpec.constructorBuilder()
        .addParameter(spyInstanceParam)
        .build()

    val instanceInitializer = PropertySpec
        .builder(spyInstanceName, spyInstanceType)
        .initializer(spyInstanceName)
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

    val suppressDeprecationError = AnnotationSpec.builder(SUPPRESS_ANNOTATION)
        .addMember("%S", "DEPRECATION_ERROR")
        .build()

    return typeSpec
        .primaryConstructor(constructorSpec)
        .addProperties(properties.plus(instanceInitializer))
        .addFunctions(functions)
        .addAnnotation(suppressDeprecationError)
        .addKdoc(declaration.docString?.trim() ?: "")
        .addOriginatingKSFiles(usages)
        .build()
}

private fun ProcessableType.buildPropertySpecs(): List<PropertySpec> {
    return properties
        .map { it.buildPropertySpec() }
        .toList()
}

private fun ProcessableType.buildFunSpecs(): List<FunSpec> {
    return functions
        .map { it.buildFunSpec() }
        .toList()
}
