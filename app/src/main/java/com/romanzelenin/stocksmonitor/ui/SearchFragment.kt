package com.romanzelenin.stocksmonitor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.romanzelenin.stocksmonitor.MainActivityViewModel
import com.romanzelenin.stocksmonitor.PopularReqAdapter
import com.romanzelenin.stocksmonitor.R
import com.romanzelenin.stocksmonitor.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwnerLiveData.observe(viewLifecycleOwner){
            if(it.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)){
                binding.apply {
                    val popularReqAdapter = initRecycler(recyclerPopularReq)

                    viewModel.popularRequests.observe(viewLifecycleOwner, {
                        failLoadPopularRequests.isVisible = it.isEmpty()
                        popularReqAdapter.dataSet = it
                        recyclerPopularReq.adapter?.notifyDataSetChanged()
                    })

                    val youVeSearchAdapter = initRecycler(recyclerYouVeSear)
                    viewModel.youSearchedForThisRequests.observe(viewLifecycleOwner, {
                        youVeSearchAdapter.dataSet = it
                        recyclerYouVeSear.adapter?.notifyDataSetChanged()
                    })
                }
            }
        }
    }

    private fun initRecycler(recyclerView: RecyclerView): PopularReqAdapter {
        val adapter = PopularReqAdapter(
            listOf(),
            requireActivity().findViewById(R.id.app_bar_search)
        )
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL)
        recyclerView.adapter = adapter
        return adapter
    }

    companion object {
        private val TAG = SearchFragment::class.java.simpleName
    }
}