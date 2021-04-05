package com.romanzelenin.stocksmonitor.ui.card

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.romanzelenin.stocksmonitor.ui.chart.ChartFragment
import com.romanzelenin.stocksmonitor.ui.news.NewsFragment

class CardPagerAdapter(fragment: CardActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0){
            ChartFragment.newInstance()
        }else{
            NewsFragment.newInstance(1)
        }
    }
}