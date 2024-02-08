package com.example.currencyexchanger.data

import com.example.currencyexchanger.models.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET("/currency-exchange-rates")
    suspend fun getRates(
        @Query("base") base: String
    ): Response<CurrencyResponse>
}