package com.romanzelenin.stocksmonitor.model

data class QuerySym(
    val description:String,
    val displaySymbol:String,
    val symbol:String
)

data class SymLookupResp(
    val count:Int,
    val result: List<QuerySym>
)
