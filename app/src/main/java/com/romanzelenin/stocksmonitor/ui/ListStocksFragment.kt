package com.romanzelenin.stocksmonitor.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.romanzelenin.stocksmonitor.MainActivityViewModel
import com.romanzelenin.stocksmonitor.PagerCollectionAdapter
import com.romanzelenin.stocksmonitor.PagerCollectionAdapter.Companion.ARG_TAB_NAME
import com.romanzelenin.stocksmonitor.StocksPagerAdapter
import com.romanzelenin.stocksmonitor.databinding.ScrollingListStocksBinding
import com.romanzelenin.stocksmonitor.db.Repository
import com.romanzelenin.stocksmonitor.model.Stock
import kotlinx.coroutines.launch


class ListStocksFragment : Fragment() {

    private var _binding: ScrollingListStocksBinding? = null

    private val viewModel: MainActivityViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainActivityViewModel(
                    requireActivity().application,
                    Repository(requireActivity().applicationContext)
                ) as T
            }
        }
    }
    private lateinit var stocksPagerAdapter: StocksPagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScrollingListStocksBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        stocksPagerAdapter = StocksPagerAdapter(viewModel, object : DiffUtil.ItemCallback<Stock>() {
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


        _binding?.listStocks?.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = stocksPagerAdapter
        }
        arguments?.takeIf { it.containsKey(ARG_TAB_NAME) }?.apply {
            if (getInt(ARG_TAB_NAME) == PagerCollectionAdapter.STOCKS_TAB) {
                stocksPagerAdapter.addLoadStateListener {
                    if (getConnectionType(requireContext()) != 0) {
                        _binding?.listStocks?.isVisible = it.mediator?.refresh is LoadState.NotLoading
                        _binding?.progressBar?.isVisible = it.mediator?.refresh is LoadState.Loading
                    } else {
                        _binding?.listStocks?.isVisible = true
                        _binding?.progressBar?.isVisible = false

                    }
                }
                lifecycleScope.launch {
                    viewModel.getTrendingStocks().asLiveData().observe(viewLifecycleOwner, {
                        stocksPagerAdapter.submitData(lifecycle, it.map {
                            viewModel.getStock(it.symbol)!!.apply {
                                isFavourite = viewModel.isFavouriteStock(it.symbol)
                            }
                        })
                    })
                }
            } else if (getInt(ARG_TAB_NAME) == PagerCollectionAdapter.FAVOURITE_TAB) {
                lifecycleScope.launch {
                    viewModel.getFavouriteStocks().asLiveData().observe(viewLifecycleOwner, {
                        stocksPagerAdapter.submitData(lifecycle, it.map {
                            viewModel.getStock(it.symbol)!!
                        })
                    })
                }

            }
        }
    }

    fun getConnectionType(context: Context): Int {
        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    result = 2
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    result = 1
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                    result = 3
                }
            }
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}