package com.romanzelenin.stocksmonitor.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CompanyNews(
    @PrimaryKey(autoGenerate = true) val id:Int,
    var datetime: String,
    val headline: String,
    val image: String,
    val source: String,
    val summary: String,
    val url: String,
    var symbol: String
)
