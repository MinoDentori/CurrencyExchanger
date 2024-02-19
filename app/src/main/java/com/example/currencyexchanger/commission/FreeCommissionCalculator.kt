package com.example.currencyexchanger.commission

import com.example.currencyexchanger.models.User

class FreeCommissionCalculator : CommissionCalculator {
    override fun calculateCommission(amount: Double, user: User): Double {
        return 0.0
    }
}