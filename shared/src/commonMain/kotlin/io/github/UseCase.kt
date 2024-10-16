package io.github

import io.mockative.Mockable

@Mockable
interface UseCase<in P, out R> {
    suspend operator fun invoke(param: P): Result<R>
}
