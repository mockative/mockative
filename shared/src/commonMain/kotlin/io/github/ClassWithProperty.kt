package io.github

@Mockable
class ClassWithProperty(val propertyWithUnitReturningFunction: ClassUsedAsPropertyWithUnitReturningFunction) {
    init {
        propertyWithUnitReturningFunction.doSomethingReturnUnit()
    }
}

@Mockable
class ClassUsedAsPropertyWithUnitReturningFunction {
    fun doSomethingReturnUnit() {
    }

    fun returnSomeString(): String {
        return "abc"
    }
}
