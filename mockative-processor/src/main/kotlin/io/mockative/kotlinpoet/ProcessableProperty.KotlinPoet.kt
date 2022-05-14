package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*
import io.mockative.INVOCATION_GETTER
import io.mockative.INVOCATION_SETTER
import io.mockative.MOCKABLE_INVOKE
import io.mockative.ProcessableProperty

internal fun ProcessableProperty.buildPropertySpec(): PropertySpec {
    return PropertySpec.builder(name, type, KModifier.OVERRIDE)
        .mutable(declaration.isMutable)
        .getter(buildGetter())
        .setter(if (declaration.isMutable) { buildSetter() } else null)
        .build()
}

private fun ProcessableProperty.buildSetter(): FunSpec {
    val value = ParameterSpec.builder("value", type)
        .build()

    return FunSpec.setterBuilder()
        .addParameter(value)
        .addStatement(
            "%M<%T>(this, %T(%S, %N), true)",
            MOCKABLE_INVOKE, type,
            INVOCATION_SETTER, name, value
        )
        .build()
}

private fun ProcessableProperty.buildGetter(): FunSpec {
    val returnsUnit = if (type == UNIT) "true" else "false"

    return FunSpec.getterBuilder()
        .addStatement("return %M<%T>(this, %T(%S), %L)", MOCKABLE_INVOKE, type, INVOCATION_GETTER, name, returnsUnit)
        .build()
}