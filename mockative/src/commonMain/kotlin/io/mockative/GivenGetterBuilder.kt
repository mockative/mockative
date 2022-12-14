package io.mockative

class GivenGetterBuilder<V>(private val mock: Mockable, private val property: String) {
    fun whenInvoked(): ResultBuilder {
        return ResultBuilder()
    }

    inner class ResultBuilder : AnyResultBuilder<V> {
        fun then(block: () -> V) {
            val expectation = Expectation.Getter(property)
            val stub = BlockingStub(expectation) { _ ->
                block()
            }
            mock.addBlockingStub(stub)
        }

        override fun thenInvoke(block: () -> V) = then { block() }
    }
}