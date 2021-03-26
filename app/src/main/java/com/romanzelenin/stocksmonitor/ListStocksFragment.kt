package com.romanzelenin.stocksmonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.romanzelenin.stocksmonitor.PagerCollectionAdapter.Companion.ARG_TAB_NAME
import com.romanzelenin.stocksmonitor.databinding.ScrollingListStocksBinding

class ListStocksFragment : Fragment() {

    private var _binding: ScrollingListStocksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScrollingListStocksBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.listStocks.apply {
            layoutManager = LinearLayoutManager(view.context)
        }
        arguments?.takeIf { it.containsKey(ARG_TAB_NAME) }?.apply {
            if (getInt(ARG_TAB_NAME) == PagerCollectionAdapter.STOCKS_TAB) {

            } else if(getInt(ARG_TAB_NAME) == PagerCollectionAdapter.FAVOURITE_TAB) {

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}