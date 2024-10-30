package io.mockative

import org.gradle.api.Project
import java.net.JarURLConnection
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.jar.JarEntry

class ResourceManager(private val project: Project, private val clazz: Class<*>) {
    fun copyRecursively(path: String, dst: Path) {
        val resourcePath = path.removePrefix("/")
        val entries = jarEntries(path)
        for (entry in entries) {
            val entryPath = entry.name.removePrefix(resourcePath).removePrefix("/")
            val target = dst.resolve(entryPath)
            Files.createDirectories(target)

            clazz.getResourceAsStream("/${entry.name}").use {
                project.debug("Copying ${entry.name} to $target ($entryPath)")
                Files.copy(it, target, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    private fun jarEntries(path: String): Sequence<JarEntry> {
        val url = clazz.getResource(path)
        val jarConnection = url.openConnection() as JarURLConnection
        val jarFile = jarConnection.jarFile
        val jarEntries = jarFile.entries()
        val resourcePath = path.removePrefix("/")
        return jarEntries.asSequence()
            .filter { !it.isDirectory && it.name.startsWith(resourcePath) }
    }
}
