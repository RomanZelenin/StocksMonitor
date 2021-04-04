package com.romanzelenin.stocksmonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.Chart
import com.anychart.data.Set
import com.anychart.enums.HoverMode
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipDisplayMode
import com.anychart.graphics.vector.GradientKey
import com.anychart.graphics.vector.LinearGradientFill
import com.romanzelenin.stocksmonitor.databinding.ChartFragmentBinding


class ChartFragment : Fragment() {

    companion object {
        fun newInstance() = ChartFragment()
    }

    private lateinit var viewModel: ChartViewModel
    private lateinit var binding:ChartFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChartFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChartViewModel::class.java)

        val seriesData = mutableListOf<DataEntry>()
        seriesData.add(CustomDataEntry("Q2 2014", 17.982, 10.941, 9.835, 4.047, 2.841))
        seriesData.add(CustomDataEntry("Q3 2014", 17.574, 8.659, 6.230, 2.627, 2.242))
        seriesData.add(CustomDataEntry("Q4 2014", 19.75, 10.35, 6.292, 3.595, 2.136))
        seriesData.add(CustomDataEntry("Q1 2015", 30.6, 17.2, 16.1, 5.4, 5.2))
        seriesData.add(CustomDataEntry("Q2 2015", 21.316, 12.204, 16.823, 3.457, 4.210))
        seriesData.add(CustomDataEntry("Q3 2015", 20.209, 10.342, 13.23, 2.872, 2.959))
        seriesData.add(CustomDataEntry("Q4 2015", 21.773, 10.577, 12.518, 3.929, 2.704))
        seriesData.add(CustomDataEntry("Q1 2016", 29.3, 17.9, 18.4, 4.8, 5.4))

        val chart = getChart(seriesData)
        binding.anyChartView.setChart(chart)
    }

    private class CustomDataEntry internal constructor(
        x: String?,
        value: Number?,
        value2: Number?,
        value3: Number?,
        value4: Number?,
        value5: Number?
    ) :
        ValueDataEntry(x, value) {
        init {
            setValue("value2", value2)
            setValue("value3", value3)
            setValue("value4", value4)
            setValue("value5", value5)
        }
    }

    private fun getChart(seriesData: MutableList<DataEntry>):Chart{
        val areaChart = AnyChart.area()

        areaChart.xAxis(0).enabled(false)
        areaChart.yAxis(0).enabled(false)
        areaChart.animation(true)

        val set = Set.instantiate()
        set.data(seriesData)
        val series1Data = set.mapAs("{ x: 'x', value: 'value' }")
        val series2Data = set.mapAs("{ x: 'x', value: 'value2' }")
        val series3Data = set.mapAs("{ x: 'x', value: 'value3' }")
        val series4Data = set.mapAs("{ x: 'x', value: 'value4' }")
        val series5Data = set.mapAs("{ x: 'x', value: 'value5' }")

        val series1 = areaChart.splineArea(set,"")
        series1.name("Americas")
        series1.stroke("2 black")
            .fill(LinearGradientFill(270, arrayOf(GradientKey("black", 0.1, 0.2), GradientKey("white", 0.6, 0.1)),true, 0.7))
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
            .valuePostfix(" bln.")
            .displayMode(TooltipDisplayMode.UNION)

        return areaChart
    }
}