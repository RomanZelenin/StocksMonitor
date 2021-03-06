package com.romanzelenin.stocksmonitor.ui.liststocks

import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romanzelenin.stocksmonitor.MainActivityViewModel
import com.romanzelenin.stocksmonitor.R
import com.romanzelenin.stocksmonitor.model.Stock
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round

class StocksPagerAdapter(
    private val viewModel: MainActivityViewModel,
    diff: DiffUtil.ItemCallback<Stock>,
) :
    PagingDataAdapter<Stock, StocksPagerAdapter.StocksRecyclerVH>(diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksRecyclerVH {
        return StocksRecyclerVH.create(parent)
    }

    override fun onBindViewHolder(holder: StocksRecyclerVH, position: Int) {

        holder.apply {
            itemView.background = null
            if (position % 2 == 0) {
                itemView.background = ResourcesCompat.getDrawable(
                    holder.itemView.resources,
                    R.drawable.snippet_shape,
                    null
                )
            }
            logo.setImageDrawable(null)
            star.setImageDrawable(
                ResourcesCompat.getDrawable(
                    itemView.resources,
                    android.R.drawable.btn_star_big_off,
                    null
                )
            )
            ticker.text = ""
            companyName.text = ""
        }


        val item = getItem(position)

        if (item != null) {
            holder.itemView.setOnClickListener {
                it.findNavController().navigate(R.id.cardActivity, Bundle().apply {
                    putString("ticker_name", item.symbol)
                    putString("short_name", item.shortName)
                    putBoolean("favourite", item.isFavourite ?: false)
                })
            }
            holder.apply {

                if (item.imgSrc != null)
                    logo.setImageURI(Uri.parse(item.imgSrc))

                ticker.text = item.symbol
                companyName.text = item.shortName

                fun setStateStarBtn(star: ImageButton, isFavourite: Boolean) {
                    if (isFavourite) {
                        star.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                itemView.resources,
                                android.R.drawable.btn_star_big_on,
                                null
                            )
                        )
                    } else {
                        star.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                itemView.resources,
                                android.R.drawable.btn_star_big_off,
                                null
                            )
                        )
                    }
                }

                viewModel.viewModelScope.launch {
                    if (viewModel.isFavouriteStock(item.symbol)) {
                        item.isFavourite = true
                        setStateStarBtn(star, true)
                    } else {
                        item.isFavourite = false
                        setStateStarBtn(star, false)
                    }
                }

                star.setOnClickListener {
                    viewModel.viewModelScope.launch {
                        if (viewModel.isFavouriteStock(item.symbol)) {
                            setStateStarBtn(star, false)
                            viewModel.removeFromFavourite(item.symbol)
                        } else {
                            setStateStarBtn(star, true)
                            viewModel.addToFavourite(item.symbol)
                        }
                        viewModel.refreshListTrendingStocks()
                    }
                }

                val currencySymbol = viewModel.getUnicodeSymbolCurrency(item.currency)
                stockPrice.text =
                    "$currencySymbol${round(item.regularMarketPrice * 100) / 100}"


                val dailyPrice =
                    round((item.regularMarketPrice - item.regularMarketPreviousClose) * 100) / 100
                val percent = round(item.regularMarketChangePercent * 100) / 100

                val sb = StringBuffer()
                val color = when {
                    dailyPrice > 0 -> {
                        sb.append("+$currencySymbol$dailyPrice(${percent}%)")
                        ResourcesCompat.getColor(holder.itemView.resources, R.color.green, null)
                    }
                    dailyPrice < 0 -> {
                        sb.append("-$currencySymbol${abs(dailyPrice)}(${abs(percent)}%)")
                        ResourcesCompat.getColor(holder.itemView.resources, R.color.red, null)
                    }
                    else -> {
                        sb.append("$currencySymbol${dailyPrice}(${percent}%)")
                        ResourcesCompat.getColor(
                            holder.itemView.resources,
                            R.color.light_gray,
                            null
                        )
                    }
                }
                dailyPriceChange.apply {
                    text = Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
                    setTextColor(color)
                }
            }
        }
    }

    class StocksRecyclerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView
        val ticker: TextView
        val companyName: TextView
        val star: ImageButton
        val stockPrice: TextView
        val dailyPriceChange: TextView

        init {
            itemView.apply {
                logo = findViewById(R.id.logo)
                ticker = findViewById(R.id.ticker)
                companyName = findViewById(R.id.company_name)
                star = findViewById(R.id.star_btn)
                stockPrice = findViewById(R.id.stock_price)
                dailyPriceChange = findViewById(R.id.daily_price_change)
            }
        }

        companion object {
            fun create(parent: ViewGroup): StocksRecyclerVH {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.snippet, parent, false)
                return StocksRecyclerVH(view)
            }
        }
    }
}