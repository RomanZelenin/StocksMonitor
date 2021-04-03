package com.romanzelenin.stocksmonitor.db

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.collections.set

class Repository(private val context: Context) {
    private val remoteSource = FinService.getInstance(context)
    private val localSource = MonitorStocksDatabase.getInstance(context)

    private val maxAmountSearchedRequests = 20
    private val _searchedRequests = ArrayDeque<String>(maxAmountSearchedRequests)
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

    val popularRequests = liveData(timeoutInMs = 500) {
        withContext(Dispatchers.Default) {
            localSource.getPopularRequestDao()
                .getAll().map { it.map { it.name } }.let { popularRequests ->
                    emitSource(popularRequests)
                }
            remoteSource.popularRequests().map { PopularRequest(it) }.let { popularRequests ->
                localSource.withTransaction {
                    localSource.getPopularRequestDao().clear()
                    localSource.getPopularRequestDao().insertAll(popularRequests)
                }
            }
        }
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
        localSource.stockDao().removeFavourite(FavouriteStock(symbol))
    }

    suspend fun addToFavourite(symbol: String) {
        localSource.stockDao().insertFavourite(FavouriteStock(symbol))
    }

    fun refreshListTrendingStocks() {
        pagingSourceTradingStocks?.invalidate()
    }

    suspend fun getRemoteKey(stock: String) =
        localSource.getRemoteKeyDao().getKey(stock)

    suspend fun clearListTrendingStocks() {
        with(localSource){
            withTransaction {
                getRemoteKeyDao().clear()
                stockDao().clearTrendingStocks()
            }
        }
    }

    suspend fun addTrendingStocksWithRemoteKeys(stocks: List<Stock>, key: List<RemoteKey>) {
        with(localSource) {
            withTransaction {
                getRemoteKeyDao().insertAll(key)
                stockDao().insertAllStocks(stocks)
                stockDao().insertAllTrendingStocks(stocks.map { TrendingStock(it.symbol) })
            }
        }
    }

    suspend fun getStock(symbol: String): Stock? {
        return localSource.stockDao().getStock(symbol)
    }


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
    ) {
        localSource.stockDao().getAllFavouriteStock()
    }.flow


    fun searchStock(ticker: String, companyName: String) = Pager(
        config = PagingConfig(pageSize = 25),
    ) {
        localSource.stockDao().searchStock(ticker, companyName)
    }.flow

    suspend fun getCountFavouriteStock():Int {
        return localSource.stockDao().getCountFavouriteStock()
    }

    suspend fun getCountStock():Int {
        return localSource.stockDao().getCountStock()
    }

    fun saveSearchRequest(query: String) {
        val request = PopularRequest(query)
        if (!_searchedRequests.contains(request.name)) {
            if (_searchedRequests.size == maxAmountSearchedRequests) {
                _searchedRequests.removeLast()
                _searchedRequests.addFirst(request.name)
            } else {
                _searchedRequests.addFirst(request.name)
            }
            searchedRequests.postValue(_searchedRequests)
        }
    }

    fun flushSavedRequestFromMemoryToDisk() {
        File(context.filesDir.absolutePath + File.pathSeparator + "you_ve_search.txt").apply {
            delete()
            val buffer = bufferedWriter()
            _searchedRequests.forEachIndexed { index, item ->
                buffer.append(item)
                if (index < _searchedRequests.size - 1) {
                    buffer.appendLine()
                }
            }
            buffer.flush()
        }
    }
}
