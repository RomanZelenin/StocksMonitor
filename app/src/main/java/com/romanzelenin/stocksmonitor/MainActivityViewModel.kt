package com.romanzelenin.stocksmonitor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.romanzelenin.stocksmonitor.db.Repository
import com.romanzelenin.stocksmonitor.model.PopularRequest
import com.romanzelenin.stocksmonitor.model.Stock

class MainActivityViewModel(application: Application, val repository: Repository) :
    AndroidViewModel(application) {

    private val TAG = MainActivityViewModel::class.java.name

    val searchedRequests = repository.searchedRequests as LiveData<ArrayDeque<String>>

    fun getUnicodeSymbolCurrency(currency: String): String? {
        return repository.countryToCurrency.value[currency]
    }

    suspend fun lookupStock(query: String): List<Stock> {
        return repository.symLookup(query)
            .onEach {
                it.isFavourite = repository.isFavouriteStock(it.symbol)
                Log.d(TAG, "Lookup stock $it")
            }
    }

    fun getPopularRequests(onlyFromCache: Boolean): LiveData<List<String>> {
        return repository.popularRequests
    }

    @ExperimentalPagingApi
    fun getTrendingStocks() = repository.trendingStocks

    @ExperimentalPagingApi
    fun getFavouriteStocks() = repository.favouriteStocks

    fun refreshListTrendingStocks() = repository.refreshListTrendingStocks()

    suspend fun isFavouriteStock(symbol:String) = repository.isFavouriteStock(symbol)

    suspend fun removeFromFavourite(symbol: String){
        repository.removeFromFavourite(symbol)
    }

    suspend fun addToFavourite(symbol:String){
        repository.addToFavourite(symbol)
    }

    suspend fun getStock(symbol: String) = repository.getStock(symbol)

    suspend fun addStocks(stocks:List<Stock>){
        //Todo:
    }

    fun saveSearchRequest(query: String){
        repository.saveSearchRequest(query)
    }

    var query: String? = null
    @ExperimentalPagingApi
    val searchStocks = Pager(
        config = PagingConfig(pageSize = 1),
        ///remoteMediator = StocksRemoteMediator(service, db),
    ) {
        StocksPagigData(this)
    }.flow
}