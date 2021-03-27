package com.romanzelenin.stocksmonitor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import androidx.room.withTransaction
import com.romanzelenin.stocksmonitor.db.MonitorStocksDatabase
import com.romanzelenin.stocksmonitor.model.PopularRequest

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val className = MainActivityViewModel::class.java.name
    val db =
        Room.inMemoryDatabaseBuilder(application, MonitorStocksDatabase::class.java).build()
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