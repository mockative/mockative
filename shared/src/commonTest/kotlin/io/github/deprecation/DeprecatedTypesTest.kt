package io.github.deprecation

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.isMock
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress("DEPRECATION", "DEPRECATION_ERROR")
class DeprecatedTypesTest {

	@Mock
	private val deprecatedClassWarning = mock(classOf<DeprecatedClassWarning>())

	@Mock
	private val deprecatedClassError = mock(classOf<DeprecatedClassError>())

	@Mock
	private val deprecatedInterfaceWarning = mock(classOf<DeprecatedInterfaceWarning>())

	@Mock
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
