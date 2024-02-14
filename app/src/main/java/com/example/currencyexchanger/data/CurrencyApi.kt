package com.example.currencyexchanger.data

import com.example.currencyexchanger.models.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET

interface CurrencyApi {
    @GET("currency-exchange-rates")
    suspend fun getRates(): Response<CurrencyResponse>
}