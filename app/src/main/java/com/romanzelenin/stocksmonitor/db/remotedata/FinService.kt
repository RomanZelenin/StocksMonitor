package com.romanzelenin.stocksmonitor.db.remotedata

import android.content.Context
import android.util.Log
import com.romanzelenin.stocksmonitor.model.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import java.io.EOFException
import java.io.File
import java.nio.channels.UnresolvedAddressException

class FinService(pathToCacheDir:String) {
    private val TAG = FinService::class.java.simpleName

    private val cacheImagesFolder = "images"
    private val pathToImgDir = pathToCacheDir + File.separator + cacheImagesFolder

    init {
        File(pathToImgDir).mkdirs()
    }


    suspend fun symLookup(query: String): List<Stock> {

        suspend fun symToStock(item: QuerySym): Stock? {
            val quote = getQuote(item.symbol)
            return quote?.let {
                val companyProfile = getCompanyProfile(item.symbol)
                if (companyProfile?.name==null) return@let null
                companyProfile?.let {
                    val logoImg =
                        File(pathToImgDir + File.separator + it.ticker + ".png")
                    if (!logoImg.exists() && !it.logo.isNullOrEmpty()) {
                        loadLogo(it.logo)?.also { logo ->
                            logoImg.writeBytes(logo)
                        }
                        Log.d(TAG, "path to loaded logo ${logoImg.absolutePath}")
                    }
                    Stock(
                        it.ticker!!,
                        it.currency!!,
                        quote.c,
                        quote.pc,
                        ((quote.c / quote.pc)-1) * 100,
                        it.name ?: "",
                        if (logoImg.exists()) logoImg.absolutePath else null,
                        false
                    )
                }
            }
        }
        return repeatedRequest(maxAttempt = 1, timeRepeatRequestMills = 0) {
            val respond =
                client.get<SymLookupResp>("${finhub_host}search?q=$query&token=$finhub_token")
            if (respond.count == 0) emptyList()
            else respond.result.mapNotNull { symToStock(it) }
        } ?: emptyList()
    }

    suspend fun popularRequests(amount: Int = 10): List<String> {
        return repeatedRequest(maxAttempt = 1, timeRepeatRequestMills = 0){
            client.get<List<MostWatched>>("${mboum_host}tr/trending?apikey=$mboum_token")[0].quotes
                .take(amount)
                .mapNotNull { getCompanyProfile(it)?.name }
        } ?: emptyList()
    }

    suspend fun mostActiveStocks(start: Int): List<Stock>? {
        return repeatedRequest(maxAttempt = 1, timeRepeatRequestMills = 0){
            val resp =
                client.get<StockCollectionInMarket>("${mboum_host}co/collections/?list=most_actives&start=$start&apikey=$mboum_token")
            val listMostActivesStock = resp.quotes
            listMostActivesStock.map {
                //additional request to get the link to download the image
                val companyProfile = getCompanyProfile(it.symbol)
                it.imgSrc = companyProfile?.logo
                //-------------------------------------------------------
                val logoImg = File(pathToImgDir + File.separator + it.symbol + ".png")
                if (!logoImg.exists() && !it.imgSrc.isNullOrEmpty()){
                    loadLogo(it.imgSrc!!)?.also { logo ->
                        logoImg.writeBytes(logo)
                    }
                    Log.d(TAG, "path to loaded logo ${logoImg.absolutePath}")
                }
                it.apply { it.imgSrc = if(logoImg.exists()) logoImg.absolutePath else null }
            }
        }
    }


    private suspend fun <T> repeatedRequest(
        maxAttempt: Int,
        timeRepeatRequestMills: Long,
        block: suspend () -> T
    ): T? {
        var result: T? = null
        var counter = maxAttempt
        while (counter > 0) {
            try {
                result = block()
                Log.d(TAG, "$result")
                break
            } catch (e: ResponseException) {
                Log.d(TAG, e.toString())
                if (e.response.status == HttpStatusCode.TooManyRequests) {
                  /*  withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "API limit reached.\n${e.response.status.description}.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }*/
                    delay(timeRepeatRequestMills)
                } else
                    break
            } catch (e: UnresolvedAddressException) {
                Log.d(TAG, e.toString())
                /*withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Сheck internet connection ", Toast.LENGTH_SHORT)
                        .show()
                }*/
                break
            } catch (e: EOFException){
                Log.d(TAG, e.toString())
                break
            }
            counter--
        }
        if (counter == 0) {
            Log.d(TAG, "Request limit reached")
        }
        return result
    }


    private suspend fun loadLogo(url: String): ByteArray? {
        val buffer: ByteArray?
        val img: HttpResponse = client.get(url)
        buffer = img.contentLength()?.let { length ->
            ByteArray(length.toInt()).also { binaryArray ->
                img.content.readFully(binaryArray, 0, binaryArray.size)
            }
        }
        return buffer
    }

    private suspend fun getQuote(symbol: String): Quote? {
        return repeatedRequest(1, 0) {
            client.get<Quote>("${finhub_host}quote?symbol=${symbol}&token=$finhub_token")
        }
    }

    private suspend fun getCompanyProfile(symbol: String): CompanyProfile? {
        return repeatedRequest(1, 0) {
            client.get<CompanyProfile>("${finhub_host}stock/profile2?symbol=${symbol}&token=$finhub_token")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FinService? = null

        fun getInstance(context: Context): FinService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FinService(context.cacheDir.absolutePath).also { INSTANCE = it }
            }

        private val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = GsonSerializer() {
                    setPrettyPrinting()
                }
            }
        }

        private const val finhub_host = "https://finnhub.io/api/v1/"
        private const val finhub_token = "c1711r748v6se55vj2ug"

        private const val mboum_host = "https://mboum.com/api/v1/"
        private const val mboum_token = "demo"
        // "oOaFCUGPLjuaLsS3y5aqqgR1hgtbRhd4dYd7OPwCDUM4YRU8bPfbvjzfyjrU"
    }
}