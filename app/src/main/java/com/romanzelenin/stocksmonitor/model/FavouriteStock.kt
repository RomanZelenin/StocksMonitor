package com.romanzelenin.stocksmonitor.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavouriteStocks")
data class FavouriteStock(
    @PrimaryKey val symbol: String
)
