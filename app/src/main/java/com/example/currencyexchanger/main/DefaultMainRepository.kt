package com.example.currencyexchanger.main

import com.example.currencyexchanger.data.CurrencyApi
import com.example.currencyexchanger.models.CurrencyResponse
import com.example.currencyexchanger.util.Resource
import javax.inject.Inject

private const val AN_ERROR_OCCURRED = "An error occurred"

class DefaultMainRepository @Inject constructor(
    private val api: CurrencyApi
) : MainRepository {
    override suspend fun getRates(): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates()
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: AN_ERROR_OCCURRED)
        }
    }
}