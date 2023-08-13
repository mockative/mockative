package io.mockative

interface CoFun0<R> {
    suspend fun invoke(): R
}

interface CoFun1<T1, R> {
    suspend fun invoke(arg1: T1): R
}

interface CoFun2<T1, T2, R> {
    suspend fun invoke(arg1: T1, arg2: T2): R
}

interface CoFun3<T1, T2, T3, R> {
    suspend fun invoke(arg1: T1, arg2: T2, arg3: T3): R
}

interface CoFun4<T1, T2, T3, T4, R> {
    suspend fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4): R
}

interface CoFun5<T1, T2, T3, T4, T5, R> {
    suspend fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5): R
}
