package com.romanzelenin.stocksmonitor.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romanzelenin.stocksmonitor.databinding.FragmentNewsListBinding
import com.romanzelenin.stocksmonitor.repository.Repository
import com.romanzelenin.stocksmonitor.ui.card.CardActivityViewModel
import kotlinx.coroutines.launch


class NewsFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    private val viewModel: CardActivityViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CardActivityViewModel(Repository(requireContext())) as T
            }
        }
    }

    private lateinit var binding: FragmentNewsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsListBinding.inflate(inflater, container, false)
        binding.list.layoutManager = LinearLayoutManager(context)
        viewModel.getNewsCompany(requireActivity().intent.getStringExtra("ticker_name")!!)
            .observe(viewLifecycleOwner) {
                val newsAdapter = NewsItemRecyclerViewAdapter(it)
                binding.list.adapter = newsAdapter
            }

        return binding.root
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}