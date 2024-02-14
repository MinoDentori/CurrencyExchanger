package com.example.currencyexchanger.main

import com.example.currencyexchanger.models.CurrencyResponse
import com.example.currencyexchanger.util.Resource

interface MainRepository {

    suspend fun getRates() : Resource<CurrencyResponse>
}