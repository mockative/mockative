package io.mockative

internal fun <V> Map<String, V>.view(prefix: String): Map<String, V> {
    return entries
        .mapNotNull { (key, value) -> if (key.startsWith(prefix)) key.removePrefix(prefix) to value else null }
        .toMap()
}
