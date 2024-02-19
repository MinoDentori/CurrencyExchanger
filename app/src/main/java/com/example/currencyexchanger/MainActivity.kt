package com.example.currencyexchanger

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchanger.databinding.ActivityMainBinding
import com.example.currencyexchanger.main.MainViewModel
import com.example.currencyexchanger.util.CurrencyBalanceAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConvert.setOnClickListener {
            viewModel.convert(
                binding.etFrom.text.toString(),
                binding.spFromCurrency.selectedItem.toString(),
                binding.spToCurrency.selectedItem.toString()
            )
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewCurrencies)
        recyclerView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false)
        val currencyBalanceAdapter = CurrencyBalanceAdapter(emptyList())
        recyclerView.adapter = currencyBalanceAdapter

        viewModel.currencyBalances.observe(this) { currencyBalances ->
            currencyBalanceAdapter.updateData(currencyBalances)
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.conversion.collect { event ->
                    when(event) {
                        is MainViewModel.CurrencyEvent.Success -> {
                            binding.progressBar.isVisible = false
                            binding.tvResult.setTextColor(Color.BLACK)
                            binding.tvResult.text = event.resultText
                        }
                        is MainViewModel.CurrencyEvent.Failure -> {
                            binding.progressBar.isVisible = false
                            binding.tvResult.setTextColor(Color.RED)
                            binding.tvResult.text = event.errorText
                        }
                        is  MainViewModel.CurrencyEvent.Loading -> {
                            binding.progressBar.isVisible = true

                        }
                        is MainViewModel.CurrencyEvent.ShowDialog -> {
                            showDialog(event.message)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
    private fun showDialog(message: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_currency_converted, null)
        val tvMessage = dialogView.findViewById<TextView>(R.id.text_content)
        val btnOk = dialogView.findViewById<Button>(R.id.btn_done)

        tvMessage.text = message

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnOk.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
