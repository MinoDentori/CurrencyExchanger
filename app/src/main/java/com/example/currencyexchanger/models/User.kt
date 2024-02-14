package com.example.currencyexchanger.models

object UserConstants {
    const val DEFAULT_CURRENCY = "EUR"
    const val INITIAL_BALANCE = 1000.0
    const val DEFAULT_BALANCE = 0.0
}

class User(
    private val balances: HashMap<String, Double> = HashMap(),
    private var numberOfOperations: Int = 0
) {
    init {
        initializeBalances()
    }

    private fun initializeBalances() {
        Rates::class.java.declaredFields.forEach { field ->
            field.isAccessible = true
            if (field.type == Double::class.java) {
                balances[field.name] = UserConstants.DEFAULT_BALANCE
            }
        }
        balances[UserConstants.DEFAULT_CURRENCY] = UserConstants.INITIAL_BALANCE
    }

    fun updateBalance(amount: Double, currency: String): Boolean {
        return balances[currency]?.let { balance ->
            if (balance >= amount) {
                balances[currency] = balance - amount
                true
            } else {
                false
            }
        } ?: false
    }

    fun getBalance(currency: String): Double? {
        return balances[currency]
    }

    fun getCurrencyBalances(): List<CurrencyBalance> {
        val currencyBalances = mutableListOf<CurrencyBalance>()
        balances.forEach { (currency, balance) ->
            currencyBalances.add(CurrencyBalance(currency, balance))
        }
        return currencyBalances
    }

    private fun incrementNumberOfOperations(operations: Int) {
        numberOfOperations += operations
    }
}
