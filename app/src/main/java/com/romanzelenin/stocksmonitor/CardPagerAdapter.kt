package com.romanzelenin.stocksmonitor

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class CardPagerAdapter(fragment: CardActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
      //  if(position == 0){
            return ChartFragment.newInstance()
       // }
    }
}