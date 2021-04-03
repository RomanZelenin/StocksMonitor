package com.romanzelenin.stocksmonitor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import com.romanzelenin.stocksmonitor.db.Repository

class MainActivityViewModel(application: Application, private val repository: Repository) :
    AndroidViewModel(application) {

    val popularRequests = repository.popularRequests
    val youSearchedForThisRequests = repository.searchedRequests as LiveData<ArrayDeque<String>>
    val searchQuery = MutableLiveData<String>()

    fun getUnicodeSymbolCurrency(currency: String): String? {
        return repository.countryToCurrency.value[currency]
    }

    @ExperimentalPagingApi
    fun getTrendingStocks() = repository.trendingStocks

    @ExperimentalPagingApi
    fun getFavouriteStocks() = repository.favouriteStocks

    fun refreshListTrendingStocks() = repository.refreshListTrendingStocks()

    suspend fun isFavouriteStock(symbol: String) = repository.isFavouriteStock(symbol)

    suspend fun removeFromFavourite(symbol: String) = repository.removeFromFavourite(symbol)

    suspend fun addToFavourite(symbol: String) = repository.addToFavourite(symbol)

    suspend fun getStock(symbol: String) = repository.getStock(symbol)

    fun saveSearchRequest(query: String) = repository.saveSearchRequest(query)

    fun flushSavedRequestFromMemoryToDisk() = repository.flushSavedRequestFromMemoryToDisk()

    fun searchStock(ticker: String, companyName: String) =
        repository.searchStock(ticker, companyName)

    suspend fun getCountFavouriteStock():Int = repository.getCountFavouriteStock()

    suspend fun getCountTrendingStock():Int = repository.getCountTrendingStock()

    companion object {
        private val TAG = MainActivityViewModel::class.java.name
    }

}