package com.romanzelenin.stocksmonitor.ui

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.romanzelenin.stocksmonitor.MainActivityViewModel
import com.romanzelenin.stocksmonitor.R
import com.romanzelenin.stocksmonitor.StocksAdapter
import com.romanzelenin.stocksmonitor.StocksPagerAdapter
import com.romanzelenin.stocksmonitor.databinding.FragmentSearchResultBinding
import com.romanzelenin.stocksmonitor.model.Stock


class SearchResultFragment : Fragment() {


    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()


    var stocksAdapter: StocksAdapter? = null
    var dataSet = mutableListOf<Stock>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_bottom)
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
        requireActivity().findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
            setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
        stocksAdapter = StocksAdapter(viewModel, dataSet)
        binding.includeScrollingList.listStocks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stocksAdapter
        }


        arguments?.let {
            sendQuery(it.getString("QUERY")!!)
        }
    }


    fun sendQuery(query: String) {
        viewModel.searchStocks(query, query).observe(viewLifecycleOwner) {
            dataSet.clear()
            dataSet.addAll(it)
            stocksAdapter?.notifyDataSetChanged()
            binding.dataNotFound.isVisible = dataSet.isEmpty()

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(query: String) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString("QUERY", query.trim())
                }
            }
    }
}

