package com.example.currencyexchanger.poller

import com.example.currencyexchanger.models.CurrencyResponse
import com.example.currencyexchanger.util.Resource
import kotlinx.coroutines.flow.Flow

interface Poller {
    suspend fun pollRatesPeriodically(): Flow<Resource<CurrencyResponse>>
}