package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import io.mockative.*

internal fun ProcessableFunction.buildFunSpec(): FunSpec {
    val modifiers = buildModifiers()
    val returnsUnit = if (returnType == UNIT) "true" else "false"

    val invocation = if (isSuspend) "suspend" else "invoke"

    val argumentsList = buildArgumentList()
    val listOfArguments = buildListOfArgument(argumentsList)
    val parameterSpecs = buildParameterSpecs()

    val builder = FunSpec.builder(name)

    if (receiver != null) {
        builder.receiver(receiver)
    }

    if (deprecatedAnnotation != null) {
        builder.addAnnotation(deprecatedAnnotation.toAnnotationSpec())
    }

    builder
        .addModifiers(modifiers)
        .returns(returnType.applySafeAnnotations())
        .addParameters(parameterSpecs)
        .addTypeVariables(typeVariables)

    // Mockable.invoke<Int>
    val invocationCall = CodeBlock.of("%T.%L<%T>", MOCK_STATE, invocation, returnType)

    // Invocation.Function("count", listOf<Any?>(*`strings`))
    val invocationConstruction = CodeBlock.of("%T(%S, %L)", INVOCATION_FUNCTION, name, listOfArguments)

    // spyInstance?.let { { ... } }
    val spy = buildSpyBlock(argumentsList)

    if (isFromAny) {
        val superCall = CodeBlock.of("{ super.%L(%L) }", name, argumentsList)
        builder.addStatement("return %L(this, %L, %L, %L)", invocationCall, invocationConstruction, superCall, spy)
    } else {
        builder.addStatement("return %L(this, %L, %L, %L)", invocationCall, invocationConstruction, returnsUnit, spy)
    }

    return builder.build()
}

private fun ProcessableFunction.buildSpyBlock(argumentsList: CodeBlock): CodeBlock {
    val invocation = if (receiver != null) {
        // with(it) { this@doStuffToString.`doStuffToString`() }
        CodeBlock.of("with(it) { this@%L.`%L`(%L) }", name, name, argumentsList)
    } else {
        // it.`doStuffToString`()
        CodeBlock.of("it.`%L`(%L)", name, argumentsList)
    }

    return CodeBlock.of("spyInstance?.let { { %L } }", invocation)
}

private fun ProcessableFunction.buildModifiers() = buildList {
    add(KModifier.OVERRIDE)

    if (isSuspend) {
        add(KModifier.SUSPEND)
    }
}

private fun ProcessableFunction.buildArgumentList(): CodeBlock {
    val argumentsListFormat = declaration.parameters.joinToString(", ") {
        if (it.isVararg) "*`%L`" else "`%L`"
    }
    val arguments = declaration.parameters.map { it.name!!.asString() }

    return CodeBlock.builder()
        .add(argumentsListFormat, *arguments.toTypedArray())
        .build()
}

@Suppress("UnusedReceiverParameter")
private fun ProcessableFunction.buildListOfArgument(argumentList: CodeBlock): CodeBlock {
    return CodeBlock.builder()
        .add("%M<%T?>(%L)", LIST_OF, ANY, argumentList)
        .build()
}

private fun ProcessableFunction.buildParameterSpecs() = declaration.parameters
    .map { parameter ->
        val name = parameter.name!!.asString()
        val type = parameter.type.toTypeNameMockative(typeParameterResolver)
            .let {
                if (it is ClassName && it.packageName == "kotlin.jvm.functions.kotlin") {
                    ClassName("", it.simpleName)
                } else {
                    it
                }
            }

        val checkedType = type.applySafeAnnotations()

        val modifiers = buildList {
            if (parameter.isVararg) {
                add(KModifier.VARARG)
            }
        }

        ParameterSpec.builder(name, checkedType, modifiers)
            .build()
    }
