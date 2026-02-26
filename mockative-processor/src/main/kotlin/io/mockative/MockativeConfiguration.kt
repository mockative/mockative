package io.mockative

import com.squareup.kotlinpoet.MemberName
import io.mockative.kotlinpoet.bestGuess

data class MockativeConfiguration(
    val disabled: Boolean,
    val excludeMembers: Set<MemberName>,
    val excludeKotlinDefaultMembers: Boolean,
    val stubsUnitByDefault: Boolean,
    val moduleName: String,
    val isMultimodule: Boolean,
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

            val tasks = opts.getBoolean("disabled", true)

            val excludeMembers = opts.splitValues("exclude-members")
                .map { fqn -> MemberName.bestGuess(fqn) }
                .toSet()

            val excludeKotlinDefaultMembers = opts.getBoolean("exclude-kotlin-default-members", true)
            val stubsUnitByDefault = opts.getBoolean("stubs-unit-by-default", true)
            val moduleName = opts.getValue("module-name")
            val isMultimodule = opts.getBoolean("is-multimodule", false)


            return MockativeConfiguration(tasks, excludeMembers, excludeKotlinDefaultMembers, stubsUnitByDefault, moduleName, isMultimodule)
        }
    }
}
