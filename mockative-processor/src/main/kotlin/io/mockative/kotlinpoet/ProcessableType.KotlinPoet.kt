package io.mockative.kotlinpoet

import com.google.devtools.ksp.symbol.ClassKind
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import io.mockative.CONFIGURE
import io.mockative.KCLASS
import io.mockative.OPT_IN
import io.mockative.ProcessableType
import io.mockative.SUPPRESS_ANNOTATION
import io.mockative.ksp.addOriginatingKSFiles
import io.mockative.options

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

internal fun ProcessableType.buildOptInAnnotationSpec(): AnnotationSpec? {
    val globalFqns = options["io.mockative:mockative:opt-in"]?.split(",").orEmpty()
    val typeFqns = options["io.mockative:mockative:opt-in:$sourceClassName"]?.split(",").orEmpty()

    val sourceParts = sourceClassName.toString().split(".")

    val wildcardFqns = sourceParts.indices
        .map { index -> sourceParts.subList(0, index + 1).joinToString(".") }
        .mapNotNull { fqn -> options["io.mockative:mockative:opt-in:$fqn.*"] }
        .flatMap { fqns -> fqns.split(",") }

    val fqns = (globalFqns + typeFqns + wildcardFqns).distinct()
    val classNames = fqns.map { fqn -> ClassName.bestGuess(fqn) }
    if (classNames.isEmpty()) {
        return null
    }

    return AnnotationSpec.builder(OPT_IN)
        .let { annotationSpec ->
            classNames.fold(annotationSpec) { spec, className ->
                spec.addMember("%T::class", className)
            }
        }
        .build()
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
        .addStatement(
            "return %M(%T(%L)) { stubsUnitByDefault·=·%L }",
            CONFIGURE,
            mockClassName,
            addSpyInitializer,
            stubsUnitByDefault
        )
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
    val spyInstanceParam = ParameterSpec.builder("spyInstance", spyInstanceType)
        .defaultValue("null")
        .build()

    val constructorSpec = FunSpec.constructorBuilder()
        .addParameter(spyInstanceParam)
        .build()

    val instanceInitializer = PropertySpec
        .builder("spyInstance", spyInstanceType)
        .initializer("spyInstance")
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
                val (isMock, codeBlock) = valueOf(type)
                if (isMock)
                    typeSpec.addSuperclassConstructorParameter(
                        "%M(%L) { stubsUnitByDefault·=·%L }",
                        CONFIGURE,
                        codeBlock,
                        stubsUnitByDefault)
                else
                    typeSpec.addSuperclassConstructorParameter("%L", codeBlock)
            }
    } else if (declaration.classKind == ClassKind.INTERFACE) {
        typeSpec.addSuperinterface(sourceClassName.parameterizedByAny(typeVariables))
    }

    val annotations = buildList {
        // why??
        val suppressDeprecationError = AnnotationSpec.builder(SUPPRESS_ANNOTATION)
            .addMember("%S", "DEPRECATION_ERROR")
            .build()

        add(suppressDeprecationError)

        val optInSpec = buildOptInAnnotationSpec()
        if (optInSpec != null) {
            add(optInSpec)
        }
    }

    return typeSpec
        .primaryConstructor(constructorSpec)
        .addProperties(properties.plus(instanceInitializer))
        .addFunctions(functions)
        .addAnnotations(annotations)
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
