package io.mockative

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

data class OptInMap(
    private val global: List<ClassName>,
    private val qualified: List<Pair<Regex, List<ClassName>>>,
) {
    fun annotations(typeName: ClassName): List<ClassName> {
        return buildList {
            addAll(global)

            for ((regex, annotations) in qualified) {
                if (regex.matches(typeName.reflectionName())) {
                    addAll(annotations)
                }
            }
        }
    }

    fun annotations(memberName: MemberName): List<ClassName> {
        return buildList {
            addAll(global)

            for ((regex, annotations) in qualified) {
                if (regex.matches(memberName.canonicalName)) {
                    addAll(annotations)
                }
            }
        }
    }

    companion object {
        private fun String.toAnnotations(): List<ClassName> {
            return split(",").map { ClassName.bestGuess(it) }
        }

        private fun regexOf(pattern: String): Regex {
            return pattern
                .replace(".", "\\.")
                .replace("*", ".+?")
                .toRegex()
        }

        fun fromOptions(options: Map<String, String>): OptInMap {
            val global = options["opt-in"]?.toAnnotations().orEmpty()

            val qualified = options.view("opt-in:")
                .map { (pattern, str) -> regexOf(pattern) to str.toAnnotations() }

            return OptInMap(global, qualified)
        }
    }
}
