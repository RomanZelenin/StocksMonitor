package com.romanzelenin.stocksmonitor

import android.content.Context
import android.util.Log
import com.romanzelenin.stocksmonitor.model.CompanyProfile
import com.romanzelenin.stocksmonitor.model.Stock
import com.romanzelenin.stocksmonitor.model.StockCollectionInMarket
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import java.io.File

class FinService(context: Context) {
    private val className = FinService::class.java.simpleName

    private val cacheImagesFolder = "images"
    private val pathToImgDir = context.filesDir.path + File.separator + cacheImagesFolder

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
                break
            } catch (e: ClientRequestException) {
                Log.d(className, e.message!!)
                if (e.response.status == HttpStatusCode.TooManyRequests) {
                    delay(timeRepeatRequestMills)
                }
            } catch (e: ServerResponseException) {
                Log.d(className, e.message!!)
                break
            }
            counter--
            Log.d(className, "counter $counter")
        }
        return result
    }

    private suspend fun loadLogo(symbol: String): ByteArray? {
        return repeatedRequest(5, 7000) {
            client.run {
                val profile = getCompanyProfile(symbol)
                var buffer: ByteArray? = null
                if (profile != null && !profile.logo.isNullOrBlank()) {
                    //Log.d(className, "address to logo: ${profile.logo}")
                    buffer = profile.logo.let {
                        val img: HttpResponse = get(it)
                        img.contentLength()?.let { length ->
                            ByteArray(length.toInt()).also { binaryData ->
                                img.content.readFully(binaryData, 0, binaryData.size)
                            }
                        }
                    }
                }
                buffer
            }
        }
    }

    suspend fun getCompanyProfile(symbol: String): CompanyProfile? {
        return repeatedRequest(5, 7000L) {
            client.get<CompanyProfile>("${finhub_host}stock/profile2?symbol=${symbol}&token=$finhub_token")
                .also {
                    Log.d(className, it.toString())
                }
        }
    }


    suspend fun mostActiveStocks(start: Int, refreshCacheImg: Boolean): List<Stock> {
        val resp =
            client.get<StockCollectionInMarket>("${mboum_host}co/collections/?list=most_actives&start=$start&apikey=${mboum_token}")
        val listMostActivesStock = resp.quotes
        File(pathToImgDir).mkdirs()
        listMostActivesStock.forEach {
            val logoImg = File(pathToImgDir + File.separator + it.symbol + ".png")
            if (!logoImg.exists() or refreshCacheImg) {
                loadLogo(it.symbol)?.also { logo ->
                    logoImg.also {
                        it.writeBytes(logo)
                    }
                }
            }
            if (logoImg.exists())
                it.imgSrc = logoImg.absolutePath
            Log.d(className, "path to saved logo: ${it.imgSrc}")
        }
        return listMostActivesStock
    }

    companion object {

        @Volatile
        private var INSTANCE: FinService? = null

        fun getInstance(context: Context): FinService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FinService(context).also { INSTANCE = it }
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