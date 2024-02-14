package com.example.currencyexchanger.poller

import com.example.currencyexchanger.main.MainRepository
import com.example.currencyexchanger.models.CurrencyResponse
import com.example.currencyexchanger.util.DispatcherProvider
import com.example.currencyexchanger.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

private const val FETCH_DELAY_MS: Long = 5000L

private const val AN_ERROR_OCCURRED = "An error occurred"

class DefaultPoller @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: DispatcherProvider
) : Poller {

    override suspend fun pollRatesPeriodically(): Flow<Resource<CurrencyResponse>> = channelFlow {
        while (!isClosedForSend) {
            try {
                val response = repository.getRates()
                send(response)
            } catch (e: Exception) {
                send(Resource.Error(e.message ?: AN_ERROR_OCCURRED))
            }
            delay(FETCH_DELAY_MS) // Poll every 5 seconds
        }
    }.flowOn(dispatcher.io)
}