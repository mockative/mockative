package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*
import io.mockative.INVOCATION_GETTER
import io.mockative.INVOCATION_SETTER
import io.mockative.MOCKABLE
import io.mockative.ProcessableFunction
import io.mockative.ProcessableProperty
import io.mockative.log

internal fun ProcessableProperty.buildPropertySpec(): PropertySpec {
    val builder = PropertySpec.builder(name, type, KModifier.OVERRIDE)

    if (receiver != null) {
        builder.receiver(receiver)
    }

    return builder
        .mutable(declaration.isMutable)
        .getter(buildGetter())
        .setter(if (declaration.isMutable) { buildSetter() } else null)
        .build()
}

private fun ProcessableProperty.buildSetter(): FunSpec {
    val value = ParameterSpec.builder("value", type)
        .build()

    // Mockable.invoke<Int>
    val invocationCall = CodeBlock.of("%T.invoke<Unit>", MOCKABLE)

    // Invocation.Setter("prop", value)
    val invocationConstruction = CodeBlock.of("%T(%S, %N)", INVOCATION_SETTER, name, value)

    // spyInstance?.let { { ... } }
    val spyBlock = buildSpySetterBlock(value)

    return FunSpec.setterBuilder()
        .addParameter(value)
        .addStatement("%L(this, %L, true, %L)", invocationCall, invocationConstruction, spyBlock)
        .build()
}

private fun ProcessableProperty.buildSpySetterBlock(paramSpec: ParameterSpec): CodeBlock {
    val invocation = if (receiver != null) {
        // with(it) { this@doStuffToString.`doStuffToString` = value }
        CodeBlock.of("with(it) { this@%L.`%L`·=·%N }", name, name, paramSpec)
    } else {
        // it.`doStuffToString` = value
        CodeBlock.of("it.`%L`·=·%N", name, paramSpec)
    }

    return CodeBlock.of("spyInstance?.let { { %L } }", invocation)
}

private fun ProcessableProperty.buildGetter(): FunSpec {
    val returnsUnit = if (type == UNIT) "true" else "false"

    // Mockable.invoke<Int>
    val invocationCall = CodeBlock.of("%T.invoke<%T>", MOCKABLE, type)

    // Invocation.Getter("prop")
    val invocationConstruction = CodeBlock.of("%T(%S)", INVOCATION_GETTER, name)

    // spyInstance?.let { { ... } }
    val spyBlock = buildSpyGetterBlock()

    return FunSpec.getterBuilder()
        .addStatement("return %L(this, %L, %L, %L)", invocationCall, invocationConstruction, returnsUnit, spyBlock)
        .build()
}

private fun ProcessableProperty.buildSpyGetterBlock(): CodeBlock {
    val invocation = if (receiver != null) {
        // with(it) { this@doStuffToString.`doStuffToString` }
        CodeBlock.of("with(it) { this@%L.`%L` }", name, name)
    } else {
        // it.`doStuffToString`
        CodeBlock.of("it.`%L`", name)
    }

    return CodeBlock.of("spyInstance?.let { { %L } }", invocation)
}
