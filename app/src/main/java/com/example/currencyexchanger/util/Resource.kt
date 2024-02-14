package com.example.currencyexchanger.util

import com.example.currencyexchanger.models.CurrencyResponse

sealed class Resource<T> (val data: T?, val message: String?) {
    class Success<T>(data: T) : Resource<T>(data, null)
    class Error<T> (message: String) : Resource<T>(null, message)
    data object Empty : Resource<CurrencyResponse>(null, null)
}
