package com.romanzelenin.stocksmonitor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.romanzelenin.stocksmonitor.MainActivityViewModel
import com.romanzelenin.stocksmonitor.StocksAdapter
import com.romanzelenin.stocksmonitor.StocksPagerAdapter
import com.romanzelenin.stocksmonitor.databinding.FragmentSearchResultBinding
import com.romanzelenin.stocksmonitor.model.Stock
import kotlinx.coroutines.launch


class SearchResultFragment : Fragment() {


    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()


    var stocksAdapter: StocksAdapter? = null
    var dataSet = mutableListOf<Stock>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultBinding.inflate(inflater)
        return binding.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*
             stocksAdapter = StocksAdapter(viewModel, dataSet)
             binding.includeScrollingList.listStocks.apply {
                 layoutManager = LinearLayoutManager(context)
                 adapter = stocksAdapter
             }
     */
        val stocksPagerAdapter =
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
        /*  viewModel.searchStocks(query, query).observe(viewLifecycleOwner) {
              dataSet.clear()
              dataSet.addAll(it)
              stocksAdapter?.notifyDataSetChanged()
              binding.dataNotFound.isVisible = dataSet.isEmpty()

          }*/
        Toast.makeText(requireContext(), query, Toast.LENGTH_SHORT).show()
    }
}

