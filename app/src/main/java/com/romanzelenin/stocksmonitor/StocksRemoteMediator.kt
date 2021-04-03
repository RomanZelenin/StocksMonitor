package com.romanzelenin.stocksmonitor

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.romanzelenin.stocksmonitor.db.Repository
import com.romanzelenin.stocksmonitor.db.remotedata.FinService
import com.romanzelenin.stocksmonitor.model.RemoteKey
import com.romanzelenin.stocksmonitor.model.TrendingStock
import io.ktor.client.features.*
import java.io.IOException
import java.net.ConnectException
import java.nio.channels.UnresolvedAddressException

@ExperimentalPagingApi
class StocksRemoteMediator(private val service: FinService, val db: Repository) :
    RemoteMediator<Int, TrendingStock>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TrendingStock>
    ): MediatorResult {

        Log.d(TAG, loadType.name)
        val page = when (loadType) {
            LoadType.REFRESH -> {
                1
            }
            LoadType.PREPEND -> {
                state.firstItemOrNull()?.symbol?.let {
                    db.getRemoteKey(it)?.prevKey
                }
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                state.lastItemOrNull()?.symbol?.let {
                    db.getRemoteKey(it)?.nextKey
                }
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
        try {
            val stocks = service.mostActiveStocks(page)
            if (stocks != null) {
                if (loadType == LoadType.REFRESH) {
                    db.clearListTrendingStocks()
                }
                val prevKey = if (page == 1) null else page - stocks.size
                val nextKey = if (stocks.isEmpty()) null else page + stocks.size
                val keys = stocks.map { RemoteKey(it.symbol, prevKey, nextKey) }
                db.addTrendingStocksWithRemoteKeys(stocks, keys)
            }
            return MediatorResult.Success(endOfPaginationReached = stocks?.isEmpty() ?: true)
        } catch (e: IOException) {
            Log.d(TAG, e.toString())
            return MediatorResult.Error(e)
        } catch (e: ResponseException) {
            Log.d(TAG, e.toString())
            return MediatorResult.Error(e)
        } catch (e: UnresolvedAddressException) {
            Log.d(TAG, e.toString())
            return MediatorResult.Error(e)
        } catch (e: ConnectException) {
            Log.d(TAG, e.toString())
            return MediatorResult.Error(e)
        }
    }

    companion object {
        private val TAG = StocksRemoteMediator::class.java.simpleName
    }
}