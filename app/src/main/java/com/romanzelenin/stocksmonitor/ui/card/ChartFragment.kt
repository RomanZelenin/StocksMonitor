package com.romanzelenin.stocksmonitor.ui.card

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.data.Set
import com.anychart.enums.HoverMode
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipDisplayMode
import com.anychart.graphics.vector.GradientKey
import com.anychart.graphics.vector.LinearGradientFill
import com.romanzelenin.stocksmonitor.R
import com.romanzelenin.stocksmonitor.databinding.ChartFragmentBinding
import com.romanzelenin.stocksmonitor.repository.Repository
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round


class ChartFragment : Fragment() {

    companion object {
        fun newInstance() = ChartFragment()
    }

    private val viewModel: ChartViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ChartViewModel(Repository(requireContext())) as T
            }
        }
    }

    private lateinit var binding: ChartFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChartFragmentBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.apply {
            lifecycleScope.launch {
                val stock =
                    viewModel.getStock(requireActivity().intent.getStringExtra("ticker_name")!!)
                stock?.apply {
                    stockPriceChart.text =
                        viewModel.getUnicodeSymbolCurrency(stock.currency) + stock.regularMarketPrice.toString()
                    val changePrice =
                        round((stock.regularMarketPrice - stock.regularMarketPreviousClose) * 100) / 100
                    dailyPriceChangeChart.text =
                        "${viewModel.getUnicodeSymbolCurrency(stock.currency)}$changePrice (${
                            abs(
                                round(stock.regularMarketChangePercent * 100) / 100
                            )
                        }%)"
                    when {
                        changePrice < 0 -> {
                            dailyPriceChangeChart.setTextColor(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.red,
                                    null
                                )
                            )
                        }
                        changePrice > 0 -> {
                            dailyPriceChangeChart.setTextColor(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.green,
                                    null
                                )
                            )
                        }
                        else -> {
                            dailyPriceChangeChart.setTextColor(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.light_gray,
                                    null
                                )
                            )
                        }
                    }
                }
            }
            initIntervalButtons()
        }

    }


    private fun initIntervalButtons(){
        binding.apply {
            val items = arrayOf(day, week, month, year, all, sixMonth)


            day.setOnClickListener {
                clickIntervalButton(it as TextView, items)


             /*   lifecycleScope.launch {
                    val seriesData = mutableListOf<DataEntry>()
                    val historicData = viewModel.getHistoricData(
                        requireActivity().intent.getStringExtra("ticker_name")!!,
                        "1h"
                    )
                    val firstDate = historicData?.lastOrNull()?.getValue("x")
                    val dataAllDay =
                        historicData?.takeLastWhile { it.getValue("x").equals(firstDate) }
                    Log.d("!!date", dataAllDay?.size?.toString() ?: "NULL")
                    dataAllDay?.forEach {
                        Log.d("!!date___", it.getValue("value").toString())
                        seriesData.add(it)
                    }
                    refreshChart(seriesData, binding.anyChartView)
                }*/

            }
            week.setOnClickListener {
                clickIntervalButton(it as TextView, items)
            }
            month.setOnClickListener {
                clickIntervalButton(it as TextView, items)

            /*    lifecycleScope.launch {
                    val seriesData = mutableListOf<DataEntry>()
                    viewModel.getHistoricData(
                        requireActivity().intent.getStringExtra("ticker_name")!!,
                        "5m"
                    )?.forEach { seriesData.add(it) }

                    refreshChart(seriesData, binding.anyChartView)
                }*/
            }
            year.setOnClickListener {
                clickIntervalButton(it as TextView, items)
            }
            sixMonth.setOnClickListener {
                clickIntervalButton(it as TextView, items)
            }
            all.setOnClickListener {
                clickIntervalButton(it as TextView, items)
                lifecycleScope.launch {
                    val seriesData = mutableListOf<DataEntry>()
                    viewModel.getHistoricData(
                        requireActivity().intent.getStringExtra("ticker_name")!!,
                        "1wk"
                    )?.forEach { seriesData.add(it) }

                    refreshChart(seriesData, binding.anyChartView)
                }
            }

            //Set start position(don't change position!)
            all.performClick()
        }
    }

    private fun clickIntervalButton(item: TextView, allItems: Array<TextView>) {
        item.background =
            ResourcesCompat.getDrawable(resources, R.drawable.suggestion_shape_black, null)
        item.setTextColor(resources.getColor(R.color.white))

        allItems.forEach {
            if (it != item) {
                it.background =
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.suggestion_shape,
                        null
                    )
                it.setTextColor(resources.getColor(R.color.black))
            }
        }
    }

    private fun refreshChart(seriesData: MutableList<DataEntry>, anyChartView: AnyChartView) {
        val areaChart = AnyChart.area()

        areaChart.xAxis(0).enabled(false)
        areaChart.yAxis(0).enabled(false)
        areaChart.animation(true)

        val set = Set.instantiate()
        set.data(seriesData)
        set.mapAs("{ x: 'x', value: 'value' }")

        val series1 = areaChart.splineArea(set, "")
        //series1.name("Americas")
        series1.stroke("2 black")
            .fill(
                LinearGradientFill(
                    270,
                    arrayOf(GradientKey("black", 0.1, 0.2), GradientKey("white", 0.6, 0.1)),
                    true,
                    0.7
                )
            )
        series1.hovered().stroke("3 #fff")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
            .stroke("1.5 #fff")
        series1.markers().zIndex(100.0)

        areaChart.interactivity().hoverMode(HoverMode.BY_X)
        areaChart.tooltip()
            .valuePrefix("$")
            .displayMode(TooltipDisplayMode.UNION)

        anyChartView.setChart(areaChart)

    }
}