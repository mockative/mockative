package io.mockative

data class GetObjectRequest(val id: String)
data class GetObjectResponse(val body: String)

data class File(val body: String)

interface S3Client {
    fun <T> getObjectSync(input: GetObjectRequest, block: (GetObjectResponse) -> T): T

    suspend fun <T> getObject(input: GetObjectRequest, block: suspend (GetObjectResponse) -> T): T
}
