package io.github.deprecation

import io.mockative.of
import io.mockative.isMock
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress("DEPRECATION", "DEPRECATION_ERROR")
class DeprecatedTypesTest {
    private val deprecatedClassWarning = mock(of<DeprecatedClassWarning>())
    private val deprecatedClassError = mock(of<DeprecatedClassError>())
    private val deprecatedInterfaceWarning = mock(of<DeprecatedInterfaceWarning>())
    private val deprecatedInterfaceError = mock(of<DeprecatedInterfaceError>())

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
