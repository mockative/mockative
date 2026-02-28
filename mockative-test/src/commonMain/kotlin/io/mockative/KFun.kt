@file:Suppress("UnusedImport")

package io.mockative

// This import must be kept otherwise compilation fails when multimodule mode is activated
import io.mockative.Mockable

@Mockable
interface Fun0<R> {
    fun invoke(): R
}

@Mockable
interface Fun1<T1, R> {
    fun invoke(arg1: T1): R
}

@Mockable
interface Fun2<T1, T2, R> {
    fun invoke(arg1: T1, arg2: T2): R
}

@Mockable
interface Fun3<T1, T2, T3, R> {
    fun invoke(arg1: T1, arg2: T2, arg3: T3): R
}

@Mockable
interface Fun4<T1, T2, T3, T4, R> {
    fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4): R
}

@Mockable
interface Fun5<T1, T2, T3, T4, T5, R> {
    fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5): R
}
