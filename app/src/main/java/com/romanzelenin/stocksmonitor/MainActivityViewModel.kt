package com.romanzelenin.stocksmonitor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.romanzelenin.stocksmonitor.db.MonitorStocksDatabase

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
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