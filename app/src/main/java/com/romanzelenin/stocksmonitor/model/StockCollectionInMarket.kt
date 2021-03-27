package com.romanzelenin.stocksmonitor.model

data class StockCollectionInMarket(
    val start: Int,
    val count: Int,
    val total: Int,
    val quotes: List<Stock>
)
