package com.romanzelenin.stocksmonitor.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.romanzelenin.stocksmonitor.R
import com.romanzelenin.stocksmonitor.StocksRemoteMediator
import com.romanzelenin.stocksmonitor.db.localdata.MonitorStocksDatabase
import com.romanzelenin.stocksmonitor.db.remotedata.FinService
import com.romanzelenin.stocksmonitor.model.*
import kotlinx.coroutines.flow.map
import java.io.File

class Repository(context: Context) {
    private val remoteSource = FinService.getInstance(context)
    private val localSource = MonitorStocksDatabase.getInstance(context)

    private val _searchedRequests = ArrayDeque<String>(20)
    val searchedRequests = MutableLiveData(_searchedRequests)


    init {
        File(context.filesDir.absolutePath + File.pathSeparator + "you_ve_search.txt").apply {
            createNewFile()
            readLines()
                .forEach {
                    _searchedRequests.addLast(it)
                }
        }
    }

    val popularRequests: LiveData<List<String>> by lazy {
        localSource.getPopularRequestDao().getAllPopularRequest()
            .map { it.map { it.name } }
            .asLiveData()
    }

    val countryToCurrency = lazy {
        val symbols = mutableMapOf<String, String>()
        context.resources.getStringArray(R.array.countryToCurrency).forEach { item ->
            item.split(",").also {
                symbols[it[0]] = it[1]
            }
        }
        symbols
    }

    suspend fun symLookup(symbol: String): List<Stock> {
        return remoteSource.symLookup(symbol).distinctBy { it.symbol }
    }

    suspend fun isFavouriteStock(symbol: String): Boolean {
        return localSource.stockDao().getFavouriteStock(symbol) != null
    }

    suspend fun removeFromFavourite(symbol: String) {
        localSource.stockDao().removeFavouriteStock(FavouriteStock(symbol))
    }

    suspend fun addToFavourite(symbol: String) {
        localSource.stockDao().insertFavouriteStock(FavouriteStock(symbol))
    }

    fun refreshListTrendingStocks() {
        pagingSourceTradingStocks?.invalidate()
    }

    suspend fun getRemoteKey(stock: String) =
        localSource.getRemoteKeyDao().getRemoteKey(stock)

    suspend fun clearListTrendingStocks() {
        localSource.withTransaction {
            localSource.getRemoteKeyDao().clearRemoteKey()
            localSource.stockDao().clearTrendingStocks()
        }
    }

    suspend fun addTrendingStocksWithRemoteKeys(stocks: List<Stock>, key: List<RemoteKey>) {
        localSource.withTransaction {
            localSource.getRemoteKeyDao().insertAll(key)
            localSource.stockDao().insertAllStocks(stocks)
            localSource.stockDao().insertAllTrendingStocks(stocks.map { TrendingStock(it.symbol) })
        }
    }

    suspend fun getStock(symbol: String) =
        localSource.stockDao().getStock(symbol)


    private var pagingSourceTradingStocks: PagingSource<Int, TrendingStock>? = null

    @ExperimentalPagingApi
    val trendingStocks = Pager(
        config = PagingConfig(pageSize = 25),
        remoteMediator = StocksRemoteMediator(remoteSource, this),
    ) {
        localSource.stockDao().getAllTrendingStocks().also { pagingSourceTradingStocks = it }
    }.flow

    @ExperimentalPagingApi
    val favouriteStocks = Pager(
        config = PagingConfig(pageSize = 25),
        ///remoteMediator = StocksRemoteMediator(service, db),
    ) {
        localSource.stockDao().getAllFavouriteStock()
    }.flow


    fun getSearchedRequests(): LiveData<ArrayDeque<String>> {
        return searchedRequests
    }

    fun saveSearchRequest(query: String) {
        val request = PopularRequest(query)
        if (!_searchedRequests.contains(request.name)) {
            if (_searchedRequests.size == 20) {
                _searchedRequests.removeLast()
                _searchedRequests.addFirst(request.name)
            } else {
                _searchedRequests.addFirst(request.name)
            }
            searchedRequests.postValue(_searchedRequests)
        }
    }
}
