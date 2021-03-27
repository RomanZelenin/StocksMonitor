package com.romanzelenin.stocksmonitor.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKey(
    @PrimaryKey val stock_id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
