package com.romanzelenin.stocksmonitor.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TrendingStocks")
data class TrendingStock(
    @PrimaryKey val symbol: String,
)
