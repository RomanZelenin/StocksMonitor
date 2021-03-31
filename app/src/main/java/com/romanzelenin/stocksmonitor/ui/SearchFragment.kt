package com.romanzelenin.stocksmonitor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.romanzelenin.stocksmonitor.MainActivityViewModel
import com.romanzelenin.stocksmonitor.PopularReqAdapter
import com.romanzelenin.stocksmonitor.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        countBackStack = requireActivity().supportFragmentManager.backStackEntryCount
        _binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
   /*     requireActivity().findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
            setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

            binding.apply {
                val popularReqAdapter = initRecycler(recyclerPopularReq)
                lifecycleScope.launch {
                    viewModel.popularRequests.observe(viewLifecycleOwner, {
                            popularReqAdapter.dataSet = it
                            recyclerPopularReq.adapter?.notifyDataSetChanged()
                    })
                }
                val youVeSearchAdapter = initRecycler(recyclerYouVeSear)
                viewModel.searchedRequests.observe(viewLifecycleOwner, {
                    youVeSearchAdapter.dataSet = it
                    recyclerYouVeSear.adapter?.notifyDataSetChanged()
                })
            }*/

    }




    private fun initRecycler(recyclerView: RecyclerView): PopularReqAdapter {
        val adapter = PopularReqAdapter(listOf())
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL)
        recyclerView.adapter = adapter
        return adapter
    }

    companion object {
        var countBackStack: Int = 0

        @JvmStatic
        fun newInstance() =
            SearchFragment()
    }
}