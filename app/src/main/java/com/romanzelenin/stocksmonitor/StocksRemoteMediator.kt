package com.romanzelenin.stocksmonitor

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.romanzelenin.stocksmonitor.db.Repository
import com.romanzelenin.stocksmonitor.db.remotedata.FinService
import com.romanzelenin.stocksmonitor.model.RemoteKey
import com.romanzelenin.stocksmonitor.model.TrendingStock
import java.io.IOException
import java.nio.channels.UnresolvedAddressException

@ExperimentalPagingApi
class StocksRemoteMediator(private val service: FinService, val db: Repository) :
    RemoteMediator<Int, TrendingStock>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TrendingStock>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                state.anchorPosition?.run {
                    state.closestItemToPosition(this)?.symbol?.let {
                        db.getRemoteKey(it)?.nextKey?.minus(1)
                    }
                } ?: 1
            }
            LoadType.PREPEND -> {
                state.firstItemOrNull()?.symbol?.let {
                    db.getRemoteKey(it)?.prevKey
                }
                    ?: return MediatorResult.Success(endOfPaginationReached = false)
            }
            LoadType.APPEND -> {
                state.lastItemOrNull()?.symbol?.let {
                    db.getRemoteKey(it)?.nextKey
                }
                    ?: return MediatorResult.Success(endOfPaginationReached = false)
            }
        }
        try {
            val stocks = service.mostActiveStocks(page)

            if (stocks != null) {
                if (loadType == LoadType.REFRESH) {
                    db.clearListTrendingStocks()
                }

                val prevKey = if (page == 1) null else page - state.config.pageSize
                val nextKey = if (stocks.isEmpty()) null else page + state.config.pageSize
                val keys = stocks.map {
                    RemoteKey(it.symbol, prevKey, nextKey)
                }
                db.addTrendingStocksWithRemoteKeys(stocks, keys)
                return MediatorResult.Success(endOfPaginationReached = stocks.isEmpty())
            }
            return MediatorResult.Error(IllegalArgumentException())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}