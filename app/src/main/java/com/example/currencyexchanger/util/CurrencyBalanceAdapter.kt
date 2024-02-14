package com.example.currencyexchanger.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchanger.R
import com.example.currencyexchanger.models.CurrencyBalance

class CurrencyBalanceAdapter(
    private val balances: List<CurrencyBalance>
) : RecyclerView.Adapter<CurrencyBalanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency_balance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val balance = balances[position]
        holder.bind(balance)
    }

    override fun getItemCount(): Int {
        return balances.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCurrencyCode: TextView = itemView.findViewById(R.id.textViewCurrencyCode)
        private val textViewBalance: TextView = itemView.findViewById(R.id.textViewBalance)

        fun bind(balance: CurrencyBalance) {
            textViewCurrencyCode.text = balance.currencyCode
            textViewBalance.text = balance.balance.toString()
        }
    }
}
