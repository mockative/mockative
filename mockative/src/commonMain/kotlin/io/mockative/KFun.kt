package io.mockative

interface Fun0<R> {
    fun invoke(): R
}

interface Fun1<T1, R> {
    fun invoke(arg1: T1): R
}

interface Fun2<T1, T2, R> {
    fun invoke(arg1: T1, arg2: T2): R
}

interface Fun3<T1, T2, T3, R> {
    fun invoke(arg1: T1, arg2: T2, arg3: T3): R
}

interface Fun4<T1, T2, T3, T4, R> {
    fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4): R
}

interface Fun5<T1, T2, T3, T4, T5, R> {
    fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5): R
}
