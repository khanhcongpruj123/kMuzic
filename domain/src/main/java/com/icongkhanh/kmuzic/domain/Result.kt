package com.icongkhanh.kmuzic.domain

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Result.Success -> "Success: ${data}"
            is Result.Error -> "Error: ${exception.message}"
            else -> "Loading"
        }
    }
}

fun Result<*>.isSuccess() = this is Result.Success && this.data != null
