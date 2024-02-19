package com.example.currencyexchanger.commission

import com.example.currencyexchanger.models.User

class UpTo200EurosFreeCommissionCalculator : CommissionCalculator {
    override fun calculateCommission(amount: Double, user: User): Double {
        return if (amount <= 200) {
            0.0
        } else {
            1.0
        }
    }
}