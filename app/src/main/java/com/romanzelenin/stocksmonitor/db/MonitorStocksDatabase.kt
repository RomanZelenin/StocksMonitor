package com.romanzelenin.stocksmonitor.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.romanzelenin.stocksmonitor.model.RemoteKey
import com.romanzelenin.stocksmonitor.model.Stock

@Database(entities = [Stock::class, RemoteKey::class], version = 1)
abstract class MonitorStocksDatabase : RoomDatabase() {

    abstract fun stockDao(): StockDao

    abstract fun getRemoteKeyDao(): RemoteKeyDao

    companion object {

        @Volatile private var INSTANCE: MonitorStocksDatabase? = null

        fun getInstance(context: Context): MonitorStocksDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, MonitorStocksDatabase::class.java, "FinData.db")
                //.createFromAsset("db/FinData.db")
                .build()
    }
}