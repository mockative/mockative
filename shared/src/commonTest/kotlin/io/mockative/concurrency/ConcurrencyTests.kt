package io.mockative.concurrency

import io.mockative.dispatchBlockingTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConcurrencyTests {

    class Message(var body: String)

    private val message = Confined { Message("the-default-value") }

    @BeforeTest
    fun checkAtomicValue() {
        val body = message { body }
        assertEquals("the-default-value", body)
    }

    @AfterTest
    fun closeAtomicValue() {
        message.use {
            val body = message { body }
            assertEquals("another-value", body)
        }
    }

    @Test
    fun testStuff() = dispatchBlockingTest {
        message { body = "another-value" }
    }
}