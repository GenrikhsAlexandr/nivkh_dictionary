package com.aleksandrgenrikhs.nivkhdictionary.utils

import androidx.annotation.StringRes

sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(@StringRes val message: Int) : ResultState<Nothing>()
}