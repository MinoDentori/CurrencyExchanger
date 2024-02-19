package com.example.currencyexchanger.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchanger.R
import com.example.currencyexchanger.models.CurrencyBalance

class CurrencyBalanceAdapter(
    private var currencyBalances: List<CurrencyBalance>
) : RecyclerView.Adapter<CurrencyBalanceAdapter.CurrencyBalanceViewHolder>() {

    inner class CurrencyBalanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCurrencyCode: TextView = itemView.findViewById(R.id.textViewCurrencyCode)
        private val textViewBalance: TextView = itemView.findViewById(R.id.textViewBalance)

        fun bind(currencyBalance: CurrencyBalance) {
            textViewCurrencyCode.text = currencyBalance.currencyCode.uppercase()
            textViewBalance.text = currencyBalance.balance.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyBalanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency_balance, parent, false)
        return CurrencyBalanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyBalanceViewHolder, position: Int) {
        val currencyBalance = currencyBalances[position]
        holder.bind(currencyBalance)
    }

    override fun getItemCount(): Int {
        return currencyBalances.size
    }

    fun updateData(newCurrencyBalances: List<CurrencyBalance>) {
        currencyBalances = newCurrencyBalances
        notifyDataSetChanged()
    }
}
