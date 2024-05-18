package io.github

class FileService(private val client: S3Client) {
    fun getFileSync(id: String): File {
        TODO()
//        return client.getObjectSync(GetObjectRequest(id)) { File(it.body) }
    }

    suspend fun getFile(id: String): File {
        return client.getObject(GetObjectRequest(id)) { File(it.body) }
    }

    fun getFileRawSync(id: String, block: (GetObjectResponse) -> File): File {
        TODO()
//        return client.getObjectSync(GetObjectRequest(id), block)
    }
}
