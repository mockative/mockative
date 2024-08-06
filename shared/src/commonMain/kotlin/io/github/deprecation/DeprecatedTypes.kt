package io.github.deprecation

import io.github.Mockable

@Deprecated("This class is deprecated", level = DeprecationLevel.WARNING)
@Mockable
class DeprecatedClassWarning

@Deprecated("This class is deprecated", level = DeprecationLevel.ERROR)
@Mockable
class DeprecatedClassError

@Deprecated("This interface is deprecated", level = DeprecationLevel.WARNING)
interface DeprecatedInterfaceWarning

@Deprecated("This interface is deprecated", level = DeprecationLevel.ERROR)
interface DeprecatedInterfaceError
