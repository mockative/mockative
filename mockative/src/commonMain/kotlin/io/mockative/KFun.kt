package io.mockative

interface KFun0<R> {
    fun invoke(): R
}

interface KFun1<T1, R> {
    fun invoke(arg1: T1): R
}

interface KFun2<T1, T2, R> {
    fun invoke(arg1: T1, arg2: T2): R
}

interface KFun3<T1, T2, T3, R> {
    fun invoke(arg1: T1, arg2: T2, arg3: T3): R
}

interface KFun4<T1, T2, T3, T4, R> {
    fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4): R
}

interface KFun5<T1, T2, T3, T4, T5, R> {
    fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5): R
}
