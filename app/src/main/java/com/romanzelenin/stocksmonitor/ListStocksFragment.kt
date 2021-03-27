package com.romanzelenin.stocksmonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.romanzelenin.stocksmonitor.PagerCollectionAdapter.Companion.ARG_TAB_NAME
import com.romanzelenin.stocksmonitor.databinding.ScrollingListStocksBinding
import com.romanzelenin.stocksmonitor.model.Stock
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ListStocksFragment : Fragment() {

    private var _binding: ScrollingListStocksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var stocksAdapter: StocksAdapter

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
        stocksAdapter = StocksAdapter(viewModel,object : DiffUtil.ItemCallback<Stock>() {
            override fun areItemsTheSame(
                oldItem: Stock,
                newItem: Stock
            ): Boolean {
                return oldItem.symbol == newItem.symbol
            }

            override fun areContentsTheSame(
                oldItem: Stock,
                newItem: Stock
            ): Boolean {
                return oldItem == newItem
            }
        })
        binding.listStocks.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = stocksAdapter
        }
        arguments?.takeIf { it.containsKey(ARG_TAB_NAME) }?.apply {
            if (getInt(ARG_TAB_NAME) == PagerCollectionAdapter.STOCKS_TAB) {
                lifecycleScope.launch {
                    viewModel.popularStocks.collectLatest {
                        stocksAdapter.submitData(it)
                    }
                }
            } else if(getInt(ARG_TAB_NAME) == PagerCollectionAdapter.FAVOURITE_TAB) {

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}