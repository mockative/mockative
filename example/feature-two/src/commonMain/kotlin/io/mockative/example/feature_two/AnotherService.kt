package io.mockative.example.feature_two

import io.mockative.Mockable

@Mockable
interface AnotherService {
    fun doSomething(input: String): String
    suspend fun doSomethingAsync(input: Int): Boolean
}
