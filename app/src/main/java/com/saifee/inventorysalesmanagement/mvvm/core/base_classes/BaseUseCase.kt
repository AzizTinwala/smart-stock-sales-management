package com.saifee.inventorysalesmanagement.mvvm.core.base_classes

import android.util.Log
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

abstract class BaseUseCase<in Params, out T>(
    private val dispatcher: CoroutineDispatcher,
    private val maxRetries: Int = 0
) {

    suspend operator fun invoke(params: Params): ResultState<T> {
        return withContext(dispatcher) {
            executeWithRetry(params)
        }
    }

    private suspend fun executeWithRetry(params: Params): ResultState<T> {

        var attempt = 0
        var lastError: Throwable? = null

        while (attempt <= maxRetries) {
            try {
                log("Executing ${this::class.simpleName}, attempt=$attempt")

                val result = execute(params)

                return ResultState.Success(result)

            } catch (e: Exception) {

                lastError = e
                log("Error: ${e.message}")

                if (attempt == maxRetries) break

                delay(getBackoffDelay(attempt))
                attempt++
            }
        }

        return ResultState.Error(
            message = lastError?.message ?: "Unknown error",
            throwable = lastError
        )
    }

    protected abstract suspend fun execute(params: Params): T

    private fun getBackoffDelay(attempt: Int): Long {
        return 1000L * (attempt + 1)
    }

    protected open fun log(message: String) {
        try {
            Log.d("UseCase", message)
        } catch (_: Throwable) {
            // Ignored in local JVM unit tests where android.util.Log is unmocked
        }
    }
}