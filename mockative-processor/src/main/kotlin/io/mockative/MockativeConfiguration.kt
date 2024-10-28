package io.mockative

import com.squareup.kotlinpoet.MemberName
import io.mockative.kotlinpoet.bestGuess

data class MockativeConfiguration(
    val tasks: Set<String>,
    val optIn: OptInMap,
    val excludeMembers: Set<MemberName>,
    val excludeKotlinDefaultMembers: Boolean,
    val stubsUnitByDefault: Boolean,
) {
    companion object {
        private fun Map<String, String>.splitValues(key: String, delimiter: String = ","): List<String> {
            return get(key)?.takeUnless { it.isEmpty() }?.split(delimiter).orEmpty()
        }

        private fun Map<String, String>.getBoolean(key: String, defaultValue: Boolean): Boolean {
            return get(key)?.takeUnless { it.isEmpty() }?.toBoolean() ?: defaultValue
        }

        fun fromOptions(options: Map<String, String>): MockativeConfiguration {
            val opts = options.view("io.mockative:mockative:")

            val tasks = opts.splitValues("tasks").toSet()
            val optIn = OptInMap.fromOptions(opts)

            val excludeMembers = opts.splitValues("exclude-members")
                .map { fqn -> MemberName.bestGuess(fqn) }
                .toSet()

            val excludeKotlinDefaultMembers = opts.getBoolean("exclude-kotlin-default-members", true)
            val stubsUnitByDefault = opts.getBoolean("stubs-unit-by-default", true)

            return MockativeConfiguration(tasks, optIn, excludeMembers, excludeKotlinDefaultMembers, stubsUnitByDefault)
        }
    }
}

//class MockativeExtension {
//    private val excludedMembers = mutableListOf<MemberName>()
//
//    fun `package`(packageName: String, block: PackageExtension.() -> Unit) {
//
//    }
//
//    fun excludeJDK11() {
//        `package`("kotlin.collections") {
//            type("List") {
//                exclude("toArray")
//                exclude("getFirst")
//                exclude("getLast")
//                exclude("reversed")
//            }
//
//            type("MutableList") {
//                exclude("toArray")
//                exclude("getFirst")
//                exclude("getLast")
//                exclude("removeFirst")
//                exclude("removeLast")
//                exclude("addFirst")
//                exclude("addLast")
//                exclude("reversed")
//            }
//        }
//    }
//}
