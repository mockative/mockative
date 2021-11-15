import java.io.File
import java.util.*

internal fun Properties.loadFile(file: File, required: Boolean = true) {
    if (required || file.exists()) {
        file.reader().use { load(it) }
    }
}

internal fun Properties.loadEnv(key: String, env: String) {
    System.getenv(env)?.let { setProperty(key, it) }
}
