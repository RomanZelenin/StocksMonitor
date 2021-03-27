package com.romanzelenin.stocksmonitor

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.withTransaction
import com.romanzelenin.stocksmonitor.db.MonitorStocksDatabase
import com.romanzelenin.stocksmonitor.model.PopularRequest
import com.romanzelenin.stocksmonitor.model.Stock
import java.io.FileNotFoundException

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val className = MainActivityViewModel::class.java.name
    val db = MonitorStocksDatabase.getInstance(application)
    private val service = FinService.getInstance(application)

    val countryToCurrency = lazy {
        val symbols = mutableMapOf<String, String>()
        application.resources.getStringArray(R.array.countryToCurrency).forEach { item ->
            item.split(",").also {
                symbols[it[0]] = it[1]
            }
        }
        symbols
    }

    private val _searchedRequests = ArrayDeque<PopularRequest>(20)
    val searchedRequests = MutableLiveData(_searchedRequests)

    init {

        try {
            application.openFileInput("you_ve_search.txt").reader()
                .readLines()
                .forEach {
                    _searchedRequests.addLast(PopularRequest(it))
                }
        } catch (e: FileNotFoundException) {
            Log.d(className, "File not found")
        }


    }


    suspend fun lookupStock(query: String): List<Stock> {
        return service.symLookup(query)
            .filterNotNull()
            .distinctBy { it.symbol }
            .onEach {
                it.isFavourite = db.stockDao().getFavouriteStock(it.symbol) != null
                Log.d(className, "Lookup stock $it")
            }
    }

    fun saveSearchRequest(query: String) {
        val request = PopularRequest(query)
        if (!_searchedRequests.contains(request)) {
            if (_searchedRequests.size == 20) {
                _searchedRequests.removeLast()
                _searchedRequests.addFirst(request)
            } else {
                _searchedRequests.addFirst(request)
            }
            searchedRequests.postValue(_searchedRequests)
        }
        getApplication<Application>().apply {
            deleteFile("you_ve_search.txt")
            openFileOutput("you_ve_search.txt", Context.MODE_PRIVATE).bufferedWriter().apply {
                _searchedRequests.forEachIndexed { index, popularRequest ->
                    this.append(popularRequest.name)
                    if (index < _searchedRequests.size - 1) {
                        this.appendLine()
                    }
                }
                flush()
            }

        }
    }


    fun getPopularRequests(onlyFromCache: Boolean) = liveData {
        Log.d(className, "Loading from cache")
        emitSource(db.getPopularRequestDao().getAllPopularRequest().asLiveData())
        if (!onlyFromCache) {
            Log.d(className, "Loading from internet and insert")
            db.withTransaction {
                db.getPopularRequestDao().clearPopularRequest()
                db.getPopularRequestDao()
                    .insertAll(service.popularRequests().map { PopularRequest(it) })
            }
        }
        Log.d(className, "All data loaded and inserted")
    }


    @ExperimentalPagingApi
    val popularStocks = Pager(
        config = PagingConfig(pageSize = 25),
        remoteMediator = StocksRemoteMediator(service, db),
    ) {
        db.stockDao().getAllTrendingStocks()
    }.flow


    @ExperimentalPagingApi
    val favouriteStocks = Pager(
        config = PagingConfig(pageSize = 25),
        ///remoteMediator = StocksRemoteMediator(service, db),
    ) {
        db.stockDao().getAllFavouriteStock()
    }.flow
}