package com.romanzelenin.stocksmonitor.ui.card

import android.util.Log
import androidx.lifecycle.ViewModel
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.romanzelenin.stocksmonitor.model.Stock
import com.romanzelenin.stocksmonitor.repository.Repository
import io.ktor.client.features.*
import io.ktor.util.network.*
import java.net.ConnectException

class CardActivityViewModel(private val repository: Repository) : ViewModel() {

    suspend fun getHistoricData(symbol: String, interval: String): List<ValueDataEntry>? {
        try {
            return repository.getHistoricData(symbol, interval)
                ?.map { ValueDataEntry(it.date, it.c) }
        } catch (e: UnresolvedAddressException) {
            Log.d(TAG, e.toString())
        } catch (e: ClientRequestException) {
            Log.d(TAG, e.toString())
        } catch (e: ConnectException) {
            Log.d(TAG, e.toString())
        }
        return null
    }

    suspend fun getStock(symbol: String): Stock? {
        return repository.getStock(symbol)
    }

    fun getUnicodeSymbolCurrency(currency: String): String? {
        return repository.countryToCurrency.value[currency]
    }

    fun getNewsCompany(symbol: String) =
        repository.getCompanyNews(symbol)

    companion object{
        private val TAG = CardActivityViewModel::class.java.simpleName
    }

}