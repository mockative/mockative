package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*
import io.mockative.INVOCATION_FUNCTION
import io.mockative.LIST_OF
import io.mockative.MOCKABLE
import io.mockative.ProcessableFunction

internal fun ProcessableFunction.buildFunSpec(spyInstanceName: String): FunSpec {
    val modifiers = buildModifiers()
    val returnsUnit = if (returnType == UNIT) "true" else "false"

    val invocation = if (isSuspend) "suspend" else "invoke"

    val argumentsList = buildArgumentList()
    val listOfArguments = buildListOfArgument(argumentsList)
    val parameterSpecs = buildParameterSpecs()

    val builder = FunSpec.builder(name)

    val receiver = declaration.extensionReceiver?.toTypeNameMockative(typeParameterResolver)
    if (receiver != null) {
        builder.receiver(receiver)
    }

    builder
        .addModifiers(modifiers)
        .returns(returnType.applySafeAnnotations())
        .addParameters(parameterSpecs)
        .addTypeVariables(typeVariables)

    if (isFromAny) {
        builder.addStatement("return %T.%L<%T>(this, %T(%S, %L), { super.%L(%L) })", MOCKABLE, invocation, returnType, INVOCATION_FUNCTION, name, listOfArguments, name, argumentsList)
    } else {
        val callSpyInstance = buildCallSpyInstanceBlock(spyInstanceName, receiver != null)
        builder.addStatement("return %T.%L<%T>(this, %T(%S, %L), %L){%L}", MOCKABLE, invocation, returnType, INVOCATION_FUNCTION, name, listOfArguments, returnsUnit, callSpyInstance)
    }

    return builder.build()
}

private fun ProcessableFunction.buildCallSpyInstanceBlock(
    spyInstanceName: String,
    hasReceiver: Boolean
): String {
    return buildString {
        if (isSuspend) {
            append("kotlinx.coroutines.runBlocking{")
        }
        if (hasReceiver) {
            append("this.${name}(")
        } else {
            append("$spyInstanceName!!.${name}(")
        }
        declaration.parameters.forEach {
            if (it.isVararg) {
                append("*`${it.name?.asString()}`,")
            } else {
                append("`${it.name?.asString()}`,")
            }
        }
        append(")")
        if (isSuspend) {
            append(" }")
        }
    }

}

private fun ProcessableFunction.buildModifiers() = buildList {
    add(KModifier.OVERRIDE)

    if (isSuspend) {
        add(KModifier.SUSPEND)
    }
}

private fun ProcessableFunction.buildArgumentList(): CodeBlock {
    val argumentsListFormat = declaration.parameters.joinToString(", ") { "`%L`" }
    val arguments = declaration.parameters.map { it.name!!.asString() }

    return CodeBlock.builder()
        .add(argumentsListFormat, *arguments.toTypedArray())
        .build()
}

private fun ProcessableFunction.buildListOfArgument(argumentList: CodeBlock): CodeBlock {
    return CodeBlock.builder()
        .add("%M<%T?>(%L)", LIST_OF, ANY, argumentList)
        .build()
}

private fun ProcessableFunction.buildParameterSpecs() = declaration.parameters
    .map { parameter ->
        val name = parameter.name!!.asString()
        val type = parameter.type.toTypeNameMockative(typeParameterResolver)

        val checkedType = type.applySafeAnnotations()

        val modifiers = buildList {
            if (parameter.isVararg) {
                add(KModifier.VARARG)
            }
        }

        ParameterSpec.builder(name, checkedType, modifiers)
            .build()
    }
