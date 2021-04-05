package com.romanzelenin.stocksmonitor.repository.localdata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.romanzelenin.stocksmonitor.model.CompanyNews


@Dao
interface CompanyNewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: List<CompanyNews>)

    @Query("Select * From CompanyNews Where symbol like :symbol")
    fun getCompanyNews(symbol:String):LiveData<List<CompanyNews>>

    @Query("Delete From CompanyNews Where symbol like :symbol")
    suspend fun clear(symbol: String)
}