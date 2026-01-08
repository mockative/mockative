package io.github

import io.mockative.prefab.shared.Fun1
import io.mockative.any
import io.mockative.of
import io.mockative.coEvery
import io.mockative.eq
import io.mockative.every
import io.mockative.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals

class FileServiceTests {
    private val s3Client = mock(of<S3Client>())
    private val block = mock(of<Fun1<GetObjectResponse, File>>())

    private val fileService = FileService(s3Client)

    @Test
    fun givenGetObjectSync_whenGettingFileSync_thenFileIsReturned() = ignoreKotlinWasm {
        // Given
        val id = "9142a04a-5377-4792-89c1-2bd4f6d742fe"
        val expected = File("the-body")

        val request = GetObjectRequest(id)

        every { s3Client.getObjectSync<File>(eq(request), any()) }
            .returns(expected)

        // When
        val file = fileService.getFileSync(id)

        // Then
        assertEquals(expected, file)
    }

    @Test
    fun givenGetObjectSyncString_whenGettingFileSync_thenFileIsReturned() = ignoreKotlinWasm {
        // Given
        val id = "9142a04a-5377-4792-89c1-2bd4f6d742fe"
        val expected = File("the-body")

        val request = GetObjectRequest(id)

        every { s3Client.getObjectSync<File>(eq(request), any()) }
            .returnsMany(expected)

        // When
        val file = fileService.getFileSync(id)

        // Then
        assertEquals(expected, file)
    }

    @Test
    fun givenGetObject_whenGettingFile_thenFileIsReturned() = runTest {
        ignoreKotlinWasm {
            // Given
            val id = "9142a04a-5377-4792-89c1-2bd4f6d742fe"
            val expected = File("the-body")

            val request = GetObjectRequest(id)

            coEvery { s3Client.getObject<File>(eq(request), any()) }
                .returnsMany(expected)

            // When
            val file = fileService.getFile(id)

            // Then
            assertEquals(expected, file)
        }
    }

    @Test
    fun givenGetObjectString_whenGettingFile_thenFileIsReturned() = runTest {
        ignoreKotlinWasm {
            // Given
            val id = "9142a04a-5377-4792-89c1-2bd4f6d742fe"
            val expected = File("the-body")

            val request = GetObjectRequest(id)

            coEvery { s3Client.getObject<File>(eq(request), any()) }
                .returnsMany(expected)

            // When
            val file = withContext(Dispatchers.Default) {
                fileService.getFile(id)
            }

            // Then
            assertEquals(expected, file)
        }
    }

    @Test
    fun givenGetObjectSyncWithFun_whenGettingFileSync_thenFileIsReturned() = ignoreKotlinWasm {
        // Given
        val id = "9142a04a-5377-4792-89c1-2bd4f6d742fe"
        val expected = File("the-body")

        val request = GetObjectRequest(id)

        every { s3Client.getObjectSync<File>(eq(request), any()) }
            .returns(expected)

        // When
        val file = fileService.getFileRawSync(id, block::invoke)

        // Then
        assertEquals(expected, file)
    }
}
