package com.carlosdev.tmdbapp.domain.model

sealed class OutcomeState<out R> {
    object Progress : OutcomeState<Nothing>()
    data class Success<out T>(val data: T) : OutcomeState<T>()
    data class Error(
        val errorMessage: String,
        val errorDescription: String? = null,
        val actionText: String? = null,
        val errorCode: String? = null,
        val exception: Throwable? = null,
        val action: (() -> Unit)? = null,
    ) : OutcomeState<Nothing>()

    companion object {
        fun <T> success(data: T): OutcomeState<T> = Success(data)

        fun progress() = Progress

        fun error(
            errorMessage: String,
            errorDescription: String? = null,
            actionText: String? = null,
            errorCode: String? = null,
            action: (() -> Unit)? = null,
        ) = Error(errorMessage, errorDescription, actionText, errorCode, action = action)

        fun error(exception: Throwable) = Error(exception.message ?: "", exception = exception)
    }
}