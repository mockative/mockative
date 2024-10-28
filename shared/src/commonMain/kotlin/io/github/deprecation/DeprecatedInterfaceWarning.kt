package io.github.deprecation

import io.mockative.Mockable

@Deprecated("This interface is deprecated", level = DeprecationLevel.WARNING)
@Mockable
interface DeprecatedInterfaceWarning
