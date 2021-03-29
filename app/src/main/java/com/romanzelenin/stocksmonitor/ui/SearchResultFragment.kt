package com.romanzelenin.stocksmonitor.ui

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.romanzelenin.stocksmonitor.MainActivityViewModel
import com.romanzelenin.stocksmonitor.R
import com.romanzelenin.stocksmonitor.StocksAdapter
import com.romanzelenin.stocksmonitor.databinding.FragmentSearchResultBinding
import com.romanzelenin.stocksmonitor.model.Stock
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchResultFragment : Fragment() {


    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition =  inflater.inflateTransition(R.transition.slide_bottom)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultBinding.inflate(inflater)
        return binding.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


            arguments?.let {
            /*    setQuery(it.getString("QUERY"), false)
                viewModel.query = query.toString()*/

            }

            val stocksAdapter = StocksAdapter(viewModel, object : DiffUtil.ItemCallback<Stock>() {
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

            binding.includeScrollingList.listStocks.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = stocksAdapter
            }
            lifecycleScope.launch {
                viewModel.searchStocks.collectLatest {
                    stocksAdapter.submitData(it)
                }
            }


        requireActivity().findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
            setOnClickListener {
                requireActivity().onBackPressed()
            }
        }



    }

    companion object {


        @JvmStatic
        fun newInstance(query: String) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString("QUERY", query)
                }
            }
    }
}


class StocksPagigData(var viewModel: MainActivityViewModel):PagingSource<String,Stock>(){
    override fun getRefreshKey(state: PagingState<String, Stock>): String? {
       return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Stock> {
          /* val stocks =   viewModel.lookupStock(viewModel.query!!)
        viewModel.addStocks(stocks)*/
        //Todo:
        return LoadResult.Page(emptyList(),null, null)
    }

}