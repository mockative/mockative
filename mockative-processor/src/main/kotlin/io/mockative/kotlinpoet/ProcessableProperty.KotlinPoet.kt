package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*
import io.mockative.INVOCATION_GETTER
import io.mockative.INVOCATION_SETTER
import io.mockative.MOCKABLE
import io.mockative.ProcessableProperty

internal fun ProcessableProperty.buildPropertySpec(spyInstanceName: String): PropertySpec {
    return PropertySpec.builder(name, type, KModifier.OVERRIDE)
        .mutable(declaration.isMutable)
        .getter(buildGetter(spyInstanceName))
        .setter(if (declaration.isMutable) { buildSetter(spyInstanceName) } else null)
        .build()
}

private fun ProcessableProperty.buildSetter(spyInstanceName: String): FunSpec {
    val value = ParameterSpec.builder("value", type)
        .build()
    val callSpyInstance = "${spyInstanceName}!!.${name}=value; return@invoke value"

    return FunSpec.setterBuilder()
        .addParameter(value)
        .addStatement("%T.invoke<%T>(this, %T(%S, %N), true) { %L }", MOCKABLE, type, INVOCATION_SETTER, name, value, callSpyInstance)
        .build()
}

private fun ProcessableProperty.buildGetter(spyInstanceName: String): FunSpec {
    val returnsUnit = if (type == UNIT) "true" else "false"
    val callSpyInstance = "${spyInstanceName}!!.${name}"

    return FunSpec.getterBuilder()
        .addStatement("return %T.invoke<%T>(this, %T(%S), %L){ %L }", MOCKABLE, type, INVOCATION_GETTER, name, returnsUnit, callSpyInstance)
        .build()
}
