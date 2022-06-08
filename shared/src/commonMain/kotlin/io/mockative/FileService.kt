package io.mockative

class FileService(private val client: S3Client) {
    fun getFileSync(id: String): File {
        return client.getObjectSync(GetObjectRequest(id)) { File(it.body) }
    }

    suspend fun getFile(id: String): File {
        return client.getObject(GetObjectRequest(id)) { File(it.body) }
    }
}
