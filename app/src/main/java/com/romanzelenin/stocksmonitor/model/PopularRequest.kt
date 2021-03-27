package com.romanzelenin.stocksmonitor.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PopularRequests")
data class PopularRequest(
   @PrimaryKey val name:String
)


