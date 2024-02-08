package com.example.currencyexchanger.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchanger.models.Rates
import com.example.currencyexchanger.util.DispatcherProvider
import com.example.currencyexchanger.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NOT_A_VALID_AMOUNT = "Not a valid amount"

private const val UNEXPECTED_ERROR = "Unexpected error"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatchers: DispatcherProvider
): ViewModel() {

    sealed class CurrencyEvent {
        class Success(val resultText: String): CurrencyEvent()
        class Failure(val errorText: String): CurrencyEvent()
        data object Loading : CurrencyEvent()
        data object Empty : CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    fun convert(
        amountStr: String,
        fromCurrency: String,
        toCurrency: String
    ) {
        val fromAmount = amountStr.toFloatOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure(NOT_A_VALID_AMOUNT)
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading
            when(val ratesResponse = repository.getRates(fromCurrency)) {
                is Resource.Error -> _conversion.value = CurrencyEvent.Failure(
                    ratesResponse.message!!)
                is Resource.Success -> {
                    val rates = ratesResponse.data!!.rates
                    val rate = getRateForCurrency()
                    if (rate == null) {
                        _conversion.value = CurrencyEvent.Failure(UNEXPECTED_ERROR)
                    } else {
                        val convertedCurrency = fromAmount * rate
                        _conversion.value = CurrencyEvent.Success(
                            "$fromAmount $fromCurrency = $convertedCurrency $toCurrency"
                        )
                    }
                }
            }
        }
    }

    private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
        "AED" -> rates.aED
        "AFN" -> rates.aFN
        "ALL" -> rates.aLL
        "AMD" -> rates.aMD
        "ANG" -> rates.aNG
        "AOA" -> rates.aOA
        "ARS" -> rates.aRS
        "AUD" -> rates.aUD
        "AWG" -> rates.aWG
        "AZN" -> rates.aZN
        "BAM" -> rates.bAM
        "BBD" -> rates.bBD
        "BDT" -> rates.bDT
        "BGN" -> rates.bGN
        "BHD" -> rates.bHD
        "BIF" -> rates.bIF
        "BMD" -> rates.bMD
        "BND" -> rates.bND
        "BOB" -> rates.bOB
        "BRL" -> rates.bRL
        "BSD" -> rates.bSD
        "BTC" -> rates.bTC
        "BTN" -> rates.bTN
        "BWP" -> rates.bWP
        "BYN" -> rates.bYN
        "BYR" -> rates.bYR
        "BZD" -> rates.bZD
        "CAD" -> rates.cAD
        "CDF" -> rates.cDF
        "CHF" -> rates.cHF
        "CLF" -> rates.cLF
        "CLP" -> rates.cLP
        "CNY" -> rates.cNY
        "COP" -> rates.cOP
        "CRC" -> rates.cRC
        "CUC" -> rates.cUC
        "CUP" -> rates.cUP
        "CVE" -> rates.cVE
        "CZK" -> rates.cZK
        "DJF" -> rates.dJF
        "DKK" -> rates.dKK
        "DOP" -> rates.dOP
        "DZD" -> rates.dZD
        "EGP" -> rates.eGP
        "ERN" -> rates.eRN
        "ETB" -> rates.eTB
        "EUR" -> rates.eUR
        // Add more currencies here in the same pattern
        else -> 0.0
    }
}