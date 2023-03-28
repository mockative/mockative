package io.mockative

interface UseCase<in P, out R> {
    suspend operator fun invoke(param: P): Result<R>
}
