package io.mockative.concurrency

interface Closeable {
    fun close()
}

inline fun <T : Closeable, R> T.use(block: (T) -> R): R {
    try {
        return block(this)
    } finally {
        close()
    }
}