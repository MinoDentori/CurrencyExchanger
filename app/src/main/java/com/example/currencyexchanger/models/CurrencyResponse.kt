package com.example.currencyexchanger.models

data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: Rates = Rates()
)