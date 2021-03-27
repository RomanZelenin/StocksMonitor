package com.romanzelenin.stocksmonitor.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Stocks")
data class Stock(
    @PrimaryKey val symbol: String,
    val currency: String,
    val regularMarketPrice: Double,
    val regularMarketPreviousClose: Double,
    val regularMarketChangePercent: Double,
    val shortName: String,
    var imgSrc: String?,
    var isFavourite: Boolean?
)
