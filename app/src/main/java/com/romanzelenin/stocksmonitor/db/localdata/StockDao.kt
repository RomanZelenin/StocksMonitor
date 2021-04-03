package com.romanzelenin.stocksmonitor.db.localdata

import androidx.paging.PagingSource
import androidx.room.*
import com.romanzelenin.stocksmonitor.model.FavouriteStock
import com.romanzelenin.stocksmonitor.model.Stock
import com.romanzelenin.stocksmonitor.model.TrendingStock

@Dao
interface StockDao {

    @Query("Select * From Stocks Where symbol like '%' || :ticket || '%' or shortName like '%' || :companyName ||'%'")
    fun searchStock(ticket: String, companyName:String):PagingSource<Int, Stock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStocks(stocks: List<Stock>)

    @Query("Select * From Stocks Where symbol like :symbol")
    suspend fun getStock(symbol: String): Stock?

    @Query("Select Count(*) From TrendingStocks")
    suspend fun getCountTrendingStock(): Int

    @Query("Select * From TrendingStocks")
    fun getAllTrendingStocks(): PagingSource<Int, TrendingStock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTrendingStocks(stocks: List<TrendingStock>)

    @Query("Delete From TrendingStocks")
    suspend fun clearTrendingStocks()

    @Query("Select * From FavouriteStocks Where symbol like :symbol")
    suspend fun getFavouriteStock(symbol: String): FavouriteStock?


    @Query("Select * From FavouriteStocks Order By symbol ASC")
    fun getAllFavouriteStock(): PagingSource<Int, FavouriteStock>

    @Query("Select Count(*) From FavouriteStocks")
    suspend fun getCountFavouriteStock(): Int

    @Insert
    suspend fun insertFavourite(stock: FavouriteStock)

    @Delete
    suspend fun removeFavourite(stock: FavouriteStock)

    @Query("Select Count(*) From Stocks Where symbol like '%' || :query || '%' or shortName like '%' || :query ||'%'")
    suspend fun getCountSearchStockResult(query: String):Int

}