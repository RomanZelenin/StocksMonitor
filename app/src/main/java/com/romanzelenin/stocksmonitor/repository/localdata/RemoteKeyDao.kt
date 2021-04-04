package com.romanzelenin.stocksmonitor.repository.localdata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.romanzelenin.stocksmonitor.model.RemoteKey

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKey>)

    @Query("Select * From RemoteKey Where stock_id like :stock")
    suspend fun getKey(stock: String): RemoteKey?

    @Query("Delete From RemoteKey")
    suspend fun clear()

}