package com.romanzelenin.stocksmonitor.ui.card

import androidx.lifecycle.ViewModel
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.romanzelenin.stocksmonitor.model.Stock
import com.romanzelenin.stocksmonitor.repository.Repository

class CardActivityViewModel(private val repository: Repository) : ViewModel() {

    suspend fun getHistoricData(symbol: String, interval: String): List<ValueDataEntry>? {
        return repository.getHistoricData(symbol, interval)?.map { ValueDataEntry(it.date, it.c) }
    }

    suspend fun getStock(symbol: String): Stock? {
        return repository.getStock(symbol)
    }

    fun getUnicodeSymbolCurrency(currency: String): String? {
        return repository.countryToCurrency.value[currency]
    }

    fun getNewsCompany(symbol: String) =
        repository.getCompanyNews(symbol)


}