package com.romanzelenin.stocksmonitor.db

import androidx.paging.PagingSource
import androidx.room.*
import com.romanzelenin.stocksmonitor.model.FavouriteStock
import com.romanzelenin.stocksmonitor.model.Stock
import com.romanzelenin.stocksmonitor.model.TrendingStock

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStocks(stocks: List<Stock>)

    @Query("Select * From Stocks Where symbol like :symbol")
    suspend fun getStock(symbol: String): Stock?

    @Query("Select * From TrendingStocks")
    fun getAllTrendingStocks(): PagingSource<Int, TrendingStock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTrendingStocks(stocks: List<TrendingStock>)

    @Update
    suspend fun updateAllTrendingStocks(stocks: List<TrendingStock>)

    @Query("Delete From TrendingStocks")
    suspend fun clearTrendingStocks()

    @Query("Select * From FavouriteStocks Where symbol like :symbol")
    suspend fun getFavouriteStock(symbol: String): FavouriteStock?


    @Query("Select * From FavouriteStocks Order By symbol ASC")
    fun getAllFavouriteStock(): PagingSource<Int, FavouriteStock>

    @Insert
    @Transaction
    suspend fun insertFavouriteStock(stocks: List<FavouriteStock>)

    @Delete
    @Transaction
    suspend fun removeFavouriteStock(stocks: List<FavouriteStock>)

}