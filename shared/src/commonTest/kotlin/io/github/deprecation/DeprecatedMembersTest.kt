package io.github.deprecation

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.isMock
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class DeprecatedMembersTest {

	@Mock
	private val classWithDeprecatedMembers = mock(classOf<ClassWithDeprecatedMembers>())

	@Mock
	private val interfaceWithDeprecatedMembers = mock(classOf<InterfaceWithDeprecatedMembers>())

	@Test
	fun classWithDeprecatedMembersTest() {
		assertTrue(isMock(classWithDeprecatedMembers))
	}

	@Test
	fun interfaceWithDeprecatedMembersTest() {
		assertTrue(isMock(interfaceWithDeprecatedMembers))
	}
}
