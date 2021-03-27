package com.romanzelenin.stocksmonitor

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.romanzelenin.stocksmonitor.databinding.FragmentSearchResultBinding


class SearchResultFragment : Fragment() {


    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.includeSearchBar.appBarSearch.apply{
            setOnQueryTextFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(v.findFocus(), 0)
                }
            }

            requestFocus()
            background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.search_view_shape_bold,
                null
            )
            arguments?.let {
              setQuery(it.getString("QUERY"),false)
            }

            findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn).setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.close_icon)
            )
            findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text).apply {
                setHintTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(context, R.font.montserrat)
            }
            findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.west_back))
                setOnClickListener {
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    companion object {


        @JvmStatic
        fun newInstance(query:String) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString("QUERY",query)
                }
            }
    }
}