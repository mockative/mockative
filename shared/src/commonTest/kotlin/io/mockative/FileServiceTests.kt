package io.mockative

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FileServiceTests {
    @Mock
    private val s3Client = mock(classOf<S3Client>())

    private val fileService = FileService(s3Client)

    @Test
    fun givenGetObjectSync_whenGettingFileSync_thenFileIsReturned() {
        // Given
        val id = "9142a04a-5377-4792-89c1-2bd4f6d742fe"
        val expected = File("the-body")

        val request = GetObjectRequest(id)

        given { s3Client.getObjectSync<File>(eq(request), any { _ -> error() }) }
            .thenReturn(expected)

        // When
        val file = fileService.getFileSync(id)

        // Then
        assertEquals(expected, file)
    }

    @Test
    fun givenGetObjectSyncString_whenGettingFileSync_thenFileIsReturned() {
        // Given
        val id = "9142a04a-5377-4792-89c1-2bd4f6d742fe"
        val expected = File("the-body")

        val request = GetObjectRequest(id)

        given { s3Client.getObjectSync<File>(eq(request), any { _ -> error() }) }
            .thenReturn(expected)

        // When
        val file = fileService.getFileSync(id)

        // Then
        assertEquals(expected, file)
    }

    @Test
    fun givenGetObject_whenGettingFile_thenFileIsReturned() = runTest {
        // Given
        val id = "9142a04a-5377-4792-89c1-2bd4f6d742fe"
        val expected = File("the-body")

        val request = GetObjectRequest(id)

        coGiven { s3Client.getObject<File>(eq(request), any { _ -> error() }) }
            .thenReturn(expected)

        // When
        val file = fileService.getFile(id)

        // Then
        assertEquals(expected, file)
    }

    @Test
    fun givenGetObjectString_whenGettingFile_thenFileIsReturned() = runTest {
        // Given
        val id = "9142a04a-5377-4792-89c1-2bd4f6d742fe"
        val expected = File("the-body")

        val request = GetObjectRequest(id)

        coGiven { s3Client.getObject<File>(eq(request), any { _ -> error() }) }
            .thenReturn(expected)

        // When
        val file = fileService.getFile(id)

        // Then
        assertEquals(expected, file)
    }
}
