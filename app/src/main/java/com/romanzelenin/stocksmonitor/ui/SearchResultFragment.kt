package com.romanzelenin.stocksmonitor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.romanzelenin.stocksmonitor.MainActivityViewModel
import com.romanzelenin.stocksmonitor.StocksPagerAdapter
import com.romanzelenin.stocksmonitor.databinding.FragmentSearchResultBinding
import com.romanzelenin.stocksmonitor.model.Stock
import kotlinx.coroutines.launch


class SearchResultFragment : Fragment() {


    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()

    lateinit var stocksPagerAdapter: StocksPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultBinding.inflate(inflater)
        return binding.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.includeScrollingList.swipeContainer.setOnRefreshListener {
            binding.includeScrollingList.swipeContainer.isRefreshing = false
        }
        stocksPagerAdapter =
            StocksPagerAdapter(viewModel, object : DiffUtil.ItemCallback<Stock>() {
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
        _binding?.apply {
            includeScrollingList.listStocks.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(view.context)
                adapter = stocksPagerAdapter
            }
        }
        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            lifecycleScope.launch {
                sendQuery(query)
            }
        }
    }

    private fun sendQuery(query: String) {
        viewModel.searchStock(query, query).asLiveData().observe(viewLifecycleOwner) {
            stocksPagerAdapter.submitData(lifecycle, it)
        }
    }
}

