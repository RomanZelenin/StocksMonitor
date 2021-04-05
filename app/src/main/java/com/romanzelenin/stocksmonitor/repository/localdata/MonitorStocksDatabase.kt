package com.romanzelenin.stocksmonitor.repository.localdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.romanzelenin.stocksmonitor.model.*

@Database(entities = [Stock::class, RemoteKey::class, TrendingStock::class, FavouriteStock::class, PopularRequest::class, CompanyNews::class], version = 1)
abstract class MonitorStocksDatabase : RoomDatabase() {

    abstract fun stockDao(): StockDao

    abstract fun getRemoteKeyDao(): RemoteKeyDao

    abstract fun getPopularRequestDao(): PopularRequestDao

    abstract fun getCompanyNewsDao(): CompanyNewsDao

    companion object {

        @Volatile private var INSTANCE: MonitorStocksDatabase? = null

        fun getInstance(context: Context): MonitorStocksDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, MonitorStocksDatabase::class.java, "FinData.db")
                .createFromAsset("db/FinData.db")
                .build()
    }
}