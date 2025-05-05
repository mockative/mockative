package io.mockative

import org.gradle.api.Project
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.JarURLConnection
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.jar.JarEntry

class ResourceManager(private val project: Project, private val clazz: Class<*>) {
    fun copyRecursively(path: String, dst: Path) {
        val resourcePath = path.removePrefix("/")
        val entries = jarEntries(path)
        for (entry in entries) {
            val entryPath = entry.name.removePrefix(resourcePath).removePrefix("/")
            val target = dst.resolve(entryPath)

            clazz.getResourceAsStream("/${entry.name}").use {
                project.debug("Copying ${entry.name} to $target ($entryPath)")
                copyAndAdjustPackage(it, target)
            }
        }
    }

    private fun jarEntries(path: String): Sequence<JarEntry> {
        val resourcePath = path.removePrefix("/")
        val rp = "/${resourcePath.substringBefore("/")}"
        val url = clazz.getResource(rp)
        val jarConnection = url.openConnection() as JarURLConnection
        val jarFile = jarConnection.jarFile
        val jarEntries = jarFile.entries()
        return jarEntries.asSequence()
            .filter { !it.isDirectory && it.name.startsWith(resourcePath) }
    }

    // Project path uses ":" as separator, so we need to replace it with "." to make it a valid package name.
    // Project name and path can contain some other characters that are not valid in a package name so they need to be removed.
    // e.g. ":mockative:mockative-plugin (& tests)" -> "mockative.mockativeplugintests"
    private val projectPathAsPackage: String = project.path
        .lowercase()
        .replace(":", ".")
        .replace("[^a-z0-9.]".toRegex(), "")

    // Copy the input stream to the target path, adjusting the package declaration if necessary.
    // This is being copied to each module that uses this plugin, so to ensure that there are no duplicates,
    // the package of copied file is adjusted by adding the module's path.
    private fun copyAndAdjustPackage(inputStream: InputStream, target: Path) {
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var currentPackage = ""

            val lines = reader.lineSequence().toList().map {
                // Extract the package from the line if it contains a package declaration and adjust it by adding the module's path .
                if (it.matches("^package .*$".toRegex())) {
                    currentPackage = it.removePrefix("package ").removeSuffix(";")
                    it + projectPathAsPackage
                } else it
            }

            // Add new package suffix to the target path if the file contains the package declaration.
            val adjustedTarget =
                if (currentPackage.isNotEmpty() && target.parent.endsWith(currentPackage)) {
                    target.parent.resolveSibling(currentPackage + projectPathAsPackage).resolve(target.fileName).also {
                        project.debug("Adjusting target $target for project ${project.path}, result: $it")
                    }
                } else target

            Files.createDirectories(adjustedTarget.parent)
            Files.write(adjustedTarget, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
        }
    }
}
