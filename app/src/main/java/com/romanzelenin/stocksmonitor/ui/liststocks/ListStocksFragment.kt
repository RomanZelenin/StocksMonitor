package com.romanzelenin.stocksmonitor.ui.liststocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.map
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romanzelenin.stocksmonitor.MainActivityViewModel
import com.romanzelenin.stocksmonitor.R
import com.romanzelenin.stocksmonitor.databinding.ScrollingListStocksBinding
import com.romanzelenin.stocksmonitor.model.Stock
import com.romanzelenin.stocksmonitor.repository.Repository
import com.romanzelenin.stocksmonitor.ui.pagercollection.PagerCollectionAdapter
import com.romanzelenin.stocksmonitor.ui.pagercollection.PagerCollectionAdapter.Companion.ARG_TAB_NAME
import kotlinx.coroutines.delay
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScrollingListStocksBinding.inflate(inflater, container, false)
        return _binding!!.root
    }


    class StocksLoadStateAdapter(private val retry: () -> Unit) :
        LoadStateAdapter<StocksLoadStateAdapter.PagerStateViewHolder>() {
        override fun onBindViewHolder(holder: PagerStateViewHolder, loadState: LoadState) {
            val progress = holder.progressBar
            val btnRetry = holder.retryBtn
            val txtErrorMessage = holder.errorMessage

            btnRetry.isVisible = loadState is LoadState.Error
            txtErrorMessage.isVisible = loadState is LoadState.Error
            progress.isVisible = loadState is LoadState.Loading

            if (loadState is LoadState.Error) {
                txtErrorMessage.text = loadState.error.localizedMessage
            }

            btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            loadState: LoadState
        ): PagerStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.stocks_load_state, parent, false)
            return PagerStateViewHolder(view)
        }

        class PagerStateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val progressBar: ProgressBar = view.findViewById(R.id.progress)
            val retryBtn: Button = view.findViewById(R.id.btnRetry)
            val errorMessage: TextView = view.findViewById(R.id.errorMessage)
        }
    }


    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            listStocks.apply {
                //setHasFixedSize(true)
                layoutManager = LinearLayoutManager(view.context)
                adapter =
                    stocksPagerAdapter.withLoadStateFooter(StocksLoadStateAdapter { stocksPagerAdapter.retry() })
            }
        }

        arguments?.takeIf { it.containsKey(ARG_TAB_NAME) }?.apply {
            if (getInt(ARG_TAB_NAME) == PagerCollectionAdapter.STOCKS_TAB) {
                _binding?.swipeContainer?.setOnRefreshListener {
                    stocksPagerAdapter.refresh()
                }
                stocksPagerAdapter.addLoadStateListener {
                    if (it.mediator?.refresh is LoadState.NotLoading ||
                        it.mediator?.refresh is LoadState.Error
                    ) {
                        _binding?.swipeContainer?.isRefreshing = false
                    }
                }

                viewModel.getTrendingStocks.observe(viewLifecycleOwner, {
                    lifecycleScope.launch {
                        stocksPagerAdapter.submitData(lifecycle, it.map {
                            viewModel.getStock(it.symbol)!!.apply {
                                isFavourite = viewModel.isFavouriteStock(it.symbol)
                            }
                        })
                        lifecycleScope.launch {
                            delay(100)
                            _binding?.apply {
                                swipeUpContainer.isVisible = viewModel.getCountTrendingStock() == 0
                            }
                        }
                    }
                })

            } else if (getInt(ARG_TAB_NAME) == PagerCollectionAdapter.FAVOURITE_TAB) {
                _binding?.apply {
                    swipeContainer.setOnRefreshListener {
                        swipeContainer.isRefreshing = false
                    }
                }
                viewModel.getFavouriteStocks().asLiveData().observe(viewLifecycleOwner, {
                    lifecycleScope.launch {
                        _binding?.apply {
                            noFavouriteContainer.isVisible = viewModel.getCountFavouriteStock() == 0
                        }
                        stocksPagerAdapter.submitData(lifecycle, it.map {
                            viewModel.getStock(it.symbol)!!
                        })
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}