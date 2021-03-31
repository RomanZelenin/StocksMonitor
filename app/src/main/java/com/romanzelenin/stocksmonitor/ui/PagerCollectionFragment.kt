package com.romanzelenin.stocksmonitor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.romanzelenin.stocksmonitor.PagerCollectionAdapter
import com.romanzelenin.stocksmonitor.R
import com.romanzelenin.stocksmonitor.databinding.FragmentPagerCollectionBinding


class PagerCollectionFragment : Fragment() {

    private lateinit var pagerCollectionAdapter: PagerCollectionAdapter
    private var _binding: FragmentPagerCollectionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagerCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pagerCollectionAdapter = PagerCollectionAdapter(this)

        binding.apply {
            pager.adapter = pagerCollectionAdapter
            val face = ResourcesCompat.getFont(requireContext(), R.font.montserrat_bold)
            val stocksTabText: TextView = TextView(context).apply {
                id = android.R.id.text1
                text = getString(R.string.stocks)
                typeface = face
                textSize = 28f
                setTextColor(getColor(context, R.color.black))
            }
            val favouriteTabText: TextView = TextView(context).apply {
                id = android.R.id.text1
                text = getString(R.string.favourite)
                typeface = face
                textSize = 18f
                setTextColor(getColor(context, R.color.light_gray))
                minWidth = (resources.displayMetrics.density * 140).toInt()
            }

            TabLayoutMediator(tabs, pager) { tab, position ->
                tab.text =
                    if (position == 0) getString(R.string.stocks) else getString(R.string.favourite)
                tab.customView = if (position == 0) stocksTabText else favouriteTabText
            }.attach()

            tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab) {
                    tab.customView = if (tab.position == 0)
                        stocksTabText.apply {
                            textSize = 28f
                            setTextColor(getColor(context, R.color.black))
                        }
                    else
                        favouriteTabText.apply {
                            textSize = 28f
                            setTextColor(getColor(context, R.color.black))
                        }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    tab.customView = if (tab.position == 0)
                        stocksTabText.apply {
                            textSize = 18f
                            setTextColor(getColor(context, R.color.light_gray))
                        }
                    else
                        favouriteTabText.apply {
                            textSize = 18f
                            setTextColor(getColor(context, R.color.light_gray))
                        }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) = Unit
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PagerCollectionFragment()
    }
}