package io.github.deprecation

import io.mockative.Mockable

@Deprecated("This interface is deprecated", level = DeprecationLevel.ERROR)
@Mockable
interface DeprecatedInterfaceError
