package com.mockative.example.feature_one

import io.mockative.Mockable

@Mockable
interface SampleDataProvider {

    fun provide(id: Int): SampleData

    fun provide(value: String): SampleData
}
