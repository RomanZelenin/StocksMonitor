package com.romanzelenin.stocksmonitor.db

import androidx.paging.PagingSource
import androidx.room.*
import com.romanzelenin.stocksmonitor.model.Stock

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStocks(stocks: List<Stock>)


    @Transaction
    @Query("Select * From Stocks")
    fun getAllStocks(): PagingSource<Int, Stock>


    @Query("Delete From Stocks")
    suspend fun clearStocks()

}