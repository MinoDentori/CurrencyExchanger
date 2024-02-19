package com.example.currencyexchanger.commission

import com.example.currencyexchanger.models.User

interface CommissionCalculator {
    fun calculateCommission(amount: Double, user: User): Double
}