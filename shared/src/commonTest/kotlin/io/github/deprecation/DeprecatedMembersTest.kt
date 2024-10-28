package io.github.deprecation

import io.mockative.classOf
import io.mockative.isMock
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class DeprecatedMembersTest {
    private val classWithDeprecatedMembers = mock(classOf<ClassWithDeprecatedMembers>())
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
