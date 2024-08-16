package io.github

@Mockable
class ClassWithProperty(val propertyWithUnitReturningFunction: ClassUsedAsPropertyWithUnitReturningFunction)

@Mockable
class ClassUsedAsPropertyWithUnitReturningFunction {
    fun doSomethingReturnUnit() {
    }

    fun returnSomeString(): String {
        return "abc"
    }
}
