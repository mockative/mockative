package io.github.deprecation

import io.mockative.Mockable

@Deprecated("This class is deprecated", level = DeprecationLevel.ERROR)
@Mockable
class DeprecatedClassError
