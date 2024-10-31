package io.github.deprecation

import io.mockative.of
import io.mockative.isMock
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class DeprecatedMembersTest {
    private val classWithDeprecatedMembers = mock(of<ClassWithDeprecatedMembers>())
    private val interfaceWithDeprecatedMembers = mock(of<InterfaceWithDeprecatedMembers>())

    @Test
    fun classWithDeprecatedMembersTest() {
        assertTrue(isMock(classWithDeprecatedMembers))
    }

    @Test
    fun interfaceWithDeprecatedMembersTest() {
        assertTrue(isMock(interfaceWithDeprecatedMembers))
    }
}
