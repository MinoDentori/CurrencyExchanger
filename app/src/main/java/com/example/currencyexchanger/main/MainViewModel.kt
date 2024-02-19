package com.example.currencyexchanger.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchanger.commission.FixedPercentageCommissionCalculator
import com.example.currencyexchanger.models.CurrencyBalance
import com.example.currencyexchanger.models.User
import com.example.currencyexchanger.models.CurrencyResponse
import com.example.currencyexchanger.models.Rates
import com.example.currencyexchanger.util.DispatcherProvider
import com.example.currencyexchanger.util.Resource
import com.example.currencyexchanger.poller.Poller
import com.example.currencyexchanger.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val UNEXPECTED_ERROR = "Unexpected error occurred"
private const val INSUFFICIENT_BALANCE = "Insufficient balance"
private const val ZERO_AMOUNT = 0.0

@HiltViewModel
class MainViewModel @Inject constructor(
    private val poller: Poller,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class CurrencyEvent {
        class Success(val resultText: String) : CurrencyEvent()
        class Failure(val errorText: String) : CurrencyEvent()
        data class ShowDialog(val message: String) : CurrencyEvent()
        data object Loading : CurrencyEvent()
        data object Empty : CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    private val _rates = MutableStateFlow<Resource<CurrencyResponse>>(Resource.Empty)
    val rates: StateFlow<Resource<CurrencyResponse>> = _rates

    private val _conversionEvent = MutableLiveData<Event<String>>()
    val conversionEvent: LiveData<Event<String>> = _conversionEvent

    private val _currencyBalances = MutableLiveData<List<CurrencyBalance>>()
    val currencyBalances: LiveData<List<CurrencyBalance>>
        get() = _currencyBalances

    private val user = User()
    private val commissionCalculator = FixedPercentageCommissionCalculator()


    init {

        viewModelScope.launch(dispatchers.io) {
            pollRatesPeriodically()
        }
        updateCurrencyBalances()
    }

    private suspend fun pollRatesPeriodically() {
        poller.pollRatesPeriodically().collect { result ->
            _rates.value = result
        }
    }

    fun convert(
        amountStr: String,
        fromCurrency: String,
        toCurrency: String
    ) {
        val fromAmount = amountStr.toDoubleOrNull()
        if (fromAmount == null || fromAmount <= ZERO_AMOUNT) {
            handleInsufficientBalance()
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading

            if (!handleRatesResource()) {
                return@launch
            }

            val rates = _rates.value.data?.rates ?: return@launch
            val fromRate = getRateForCurrency(fromCurrency, rates)
            val toRate = getRateForCurrency(toCurrency, rates)
            val convertedAmount = (fromAmount * toRate) / fromRate
            val commissionAmount = commissionCalculator.calculateCommission(fromAmount, user)
            val totalFromAmountWithCommission = fromAmount + commissionAmount

            if (!checkSufficientBalance(fromCurrency, totalFromAmountWithCommission)) {
                handleInsufficientBalance()
                return@launch
            }

            if (!updateBalances(
                    fromCurrency, toCurrency, totalFromAmountWithCommission, convertedAmount)) {
                handleInsufficientBalance()
                return@launch
            }

            val message = "You have converted $fromAmount $fromCurrency to $convertedAmount $toCurrency." +
                    " Commission Fee: $commissionAmount $fromCurrency."
            user.incrementNumberOfOperations()
            updateCurrencyBalances()
            _conversion.value = CurrencyEvent.ShowDialog(message)
        }
    }

    fun getCurrencyBalances(): List<CurrencyBalance> {
        return user.getCurrencyBalances()
    }
    private fun handleInsufficientBalance() {
        _conversion.value = CurrencyEvent.Failure(INSUFFICIENT_BALANCE)
    }

    private fun handleRatesResource(): Boolean {
        val ratesResponse = _rates.value
        if (ratesResponse is Resource.Error) {
            _conversion.value = CurrencyEvent.Failure(ratesResponse.message ?: UNEXPECTED_ERROR)
            return false
        }
        return true
    }

    private fun checkSufficientBalance(
        fromCurrency: String, totalFromAmountWithCommission :Double): Boolean {
        return totalFromAmountWithCommission < user.getBalance(fromCurrency)!!
    }

    private fun updateBalances(
        fromCurrency: String,
        toCurrency: String,
        totalFromAmountWithCommission: Double,
        convertedAmount: Double
    ): Boolean {
        val userNewFromCurrencyBalance: Double?
        = user.getBalance(fromCurrency)?.minus(totalFromAmountWithCommission)
        val userNewToCurrencyBalance: Double? =
            user.getBalance(toCurrency)?.plus(convertedAmount)
        user.updateBalance(userNewFromCurrencyBalance, fromCurrency)
        user.updateBalance(userNewToCurrencyBalance, toCurrency)
        return true
    }

    private fun updateCurrencyBalances() {
        viewModelScope.launch(dispatchers.main) {
            _currencyBalances.value = user.getCurrencyBalances()
        }
    }

    private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
        "EUR" -> 1.0
        else -> {
            when (currency) {
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
                else -> 0.0
            }
        }
    }
}
