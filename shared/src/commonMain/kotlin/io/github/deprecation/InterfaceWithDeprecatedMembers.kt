package io.github.deprecation

import io.mockative.Mockable

@Mockable
interface InterfaceWithDeprecatedMembers {

    @Deprecated("This property is deprecated", level = DeprecationLevel.WARNING)
    val deprecatedPropertyWarning: String

    @Deprecated("This function is deprecated", level = DeprecationLevel.WARNING)
    fun deprecatedFunctionWarning()

    @Deprecated("This property is deprecated", level = DeprecationLevel.ERROR)
    val deprecatedPropertyError: String

    @Deprecated("This function is deprecated", level = DeprecationLevel.ERROR)
    fun deprecatedFunctionError()
}
