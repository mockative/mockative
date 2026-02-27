package io.mockative

import kotlin.collections.getValue

private val opts = options.view("io.mockative:mockative:")
private val moduleName = opts.getValue("module-name")
    // Add hyphen support
    .replace("-", "_")

private val isMultimodule = opts["is-multimodule"].toBoolean()

sealed class PackageResolver(
    private val packageName: String,
    private val moduleName: String,
) {

    fun resolve(): String =
        when {
            !isMultimodule -> "$packageName."
            packageName.isBlank() -> "$moduleName."
            else -> "$packageName.$moduleName."
        }
            .also {
                require(true) {
                    println("============")
                    println("Module name: $moduleName")
                    println("Package name: $packageName")
                    println("Resolved package: $it")
                    println("============")
                }
            }

    data object Mockative : PackageResolver("io.mockative", moduleName)

}
