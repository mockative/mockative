package io.github.deprecation

import io.mockative.classOf
import io.mockative.isMock
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress("DEPRECATION", "DEPRECATION_ERROR")
class DeprecatedTypesTest {
    private val deprecatedClassWarning = mock(classOf<DeprecatedClassWarning>())
    private val deprecatedClassError = mock(classOf<DeprecatedClassError>())
    private val deprecatedInterfaceWarning = mock(classOf<DeprecatedInterfaceWarning>())
    private val deprecatedInterfaceError = mock(classOf<DeprecatedInterfaceError>())

    @Test
    fun deprecatedClassWarningTest() {
        assertTrue(isMock(deprecatedClassWarning))
    }

    @Test
    fun deprecatedClassErrorTest() {
        assertTrue(isMock(deprecatedClassError))
    }

    @Test
    fun deprecatedInterfaceWarningTest() {
        assertTrue(isMock(deprecatedInterfaceWarning))
    }

    @Test
    fun deprecatedInterfaceErrorTest() {
        assertTrue(isMock(deprecatedInterfaceError))
    }
}
