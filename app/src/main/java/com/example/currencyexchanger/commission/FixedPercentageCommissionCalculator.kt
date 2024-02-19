package com.example.currencyexchanger.commission

import com.example.currencyexchanger.models.User

class FixedPercentageCommissionCalculator : CommissionCalculator {
    companion object {
        private const val COMMISSION_PERCENTAGE = 0.007
        private const val AMOUNT_OF_FREE_OPERATIONS = 5
    }
    override fun calculateCommission(amount: Double, user: User): Double {

        return if (user.getNumberOfOperations() > AMOUNT_OF_FREE_OPERATIONS ) {
            amount * COMMISSION_PERCENTAGE
        } else {
            0.0
        }
    }
}