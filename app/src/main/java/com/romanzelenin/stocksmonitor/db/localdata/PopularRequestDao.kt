package com.romanzelenin.stocksmonitor.db.localdata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.romanzelenin.stocksmonitor.model.PopularRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface PopularRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(request:List<PopularRequest>)

    @Query("Select * From PopularRequests")
    fun getAll():LiveData<List<PopularRequest>>

    @Query("Delete From PopularRequests")
    suspend fun clear()
}