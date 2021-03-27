package com.romanzelenin.stocksmonitor.model

import androidx.room.PrimaryKey

data class Stock(
    val symbol: String,
    val currency: String,
    val regularMarketPrice: Double,
    val regularMarketPreviousClose: Double,
    val regularMarketChangePercent: Double,
    val shortName: String,
    var imgSrc: String?,
    var isFavourite: Boolean?
)
