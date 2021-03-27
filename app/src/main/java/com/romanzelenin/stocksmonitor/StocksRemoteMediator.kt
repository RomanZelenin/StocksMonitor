package com.romanzelenin.stocksmonitor

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.romanzelenin.stocksmonitor.db.MonitorStocksDatabase
import com.romanzelenin.stocksmonitor.model.RemoteKey
import com.romanzelenin.stocksmonitor.model.Stock
import java.io.IOException

@ExperimentalPagingApi
class StocksRemoteMediator(val service: FinService, val db: MonitorStocksDatabase) :
    RemoteMediator<Int, Stock>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Stock>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                state.anchorPosition?.run {
                    state.closestItemToPosition(this)?.symbol?.let {
                        db.getRemoteKeyDao().getRemoteKeys(it)?.nextKey?.minus(1)
                    }
                } ?: 1
            }
            LoadType.PREPEND -> {
                state.firstItemOrNull()?.symbol?.let {
                    db.getRemoteKeyDao().getRemoteKeys(it)?.prevKey
                }
                    ?: return MediatorResult.Success(endOfPaginationReached = false)
            }
            LoadType.APPEND -> {
                state.lastItemOrNull()?.symbol?.let {
                    db.getRemoteKeyDao().getRemoteKeys(it)?.nextKey
                }
                    ?: return MediatorResult.Success(endOfPaginationReached = false)
            }
        }

        try {
            val stocks = service.mostActiveStocks(page,false)
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.getRemoteKeyDao().clearRemoteKey()
                    db.stockDao().clearStocks()
                }

                val prevKey = if (page == 1) null else page - state.config.pageSize
                val nextKey = if (stocks.isEmpty()) null else page + state.config.pageSize
                val keys = stocks.map {
                    RemoteKey(it.symbol, prevKey, nextKey)
                }
                db.getRemoteKeyDao().insertAll(keys)
                db.stockDao().insertAllStocks(stocks)
            }
            return MediatorResult.Success(endOfPaginationReached = stocks.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}