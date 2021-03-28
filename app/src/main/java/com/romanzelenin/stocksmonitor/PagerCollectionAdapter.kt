package com.romanzelenin.stocksmonitor

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.romanzelenin.stocksmonitor.ui.ListStocksFragment

class PagerCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    companion object{
        const val ARG_TAB_NAME = "tab"
        const val STOCKS_TAB = 0
        const val FAVOURITE_TAB = 1
    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = ListStocksFragment()
        val tabName = if (position == 0) STOCKS_TAB else FAVOURITE_TAB
        fragment.arguments = Bundle().apply {
            putInt(ARG_TAB_NAME, tabName)
        }
        return fragment
    }
}