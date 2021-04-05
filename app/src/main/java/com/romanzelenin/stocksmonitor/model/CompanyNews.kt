package com.romanzelenin.stocksmonitor.model

data class CompanyNews(
    var datetime:String,
    val headline:String,
    val image:String,
    val source:String,
    val summary:String,
    val url:String
)
