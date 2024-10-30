package io.mockative

import java.net.JarURLConnection
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.jar.JarEntry

class ResourceManager(private val clazz: Class<*>) {
    private fun println(message: Any) {
         kotlin.io.println("[ResourceManager] $message")
    }

    fun copyRecursively(path: String, dst: Path) {
        val resourcePath = path.removePrefix("/")
        val entries = jarEntries(path)
        for (entry in entries) {
            val foo = entry.name.removePrefix(resourcePath).removePrefix("/")
            val target = dst.resolve(foo)
            Files.createDirectories(target)

            clazz.getResourceAsStream("/${entry.name}").use {
                println("Copying ${entry.name} to $target ($foo)")
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
