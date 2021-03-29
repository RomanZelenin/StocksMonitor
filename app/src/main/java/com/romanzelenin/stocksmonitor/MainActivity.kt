package com.romanzelenin.stocksmonitor

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.romanzelenin.stocksmonitor.databinding.ActivityMainBinding
import com.romanzelenin.stocksmonitor.db.Repository
import com.romanzelenin.stocksmonitor.ui.PagerCollectionFragment
import com.romanzelenin.stocksmonitor.ui.SearchFragment
import com.romanzelenin.stocksmonitor.ui.SearchResultFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainActivityViewModel(application, Repository(applicationContext)) as T
            }
        }
    }

    private fun initSearchBar(){
        binding.appBarSearch.apply {
            background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.search_view_shape,
                null
            )
            findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn).setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.close_icon)
            )

            findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_search_black_24dp
                    )
                )
                isClickable = false
            }

            findViewById<TextView>(androidx.appcompat.R.id.search_src_text).apply {
                setHintTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(context, R.font.montserrat)
            }
            setQuery("",false)
            clearFocus()
        }
    }

    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.addOnBackStackChangedListener {
            if(supportFragmentManager.backStackEntryCount == 0){
                initSearchBar()
            }
        }

        binding.appBarSearch.apply {
            initSearchBar()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.saveSearchRequest(query)
                    val searchResultFragment = supportFragmentManager.findFragmentByTag(
                        SearchResultFragment::class.java.simpleName) as? SearchResultFragment
                    if(searchResultFragment==null) {
                        supportFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(
                                R.id.container,
                                SearchResultFragment.newInstance(query),
                                SearchResultFragment::class.java.simpleName
                            ).commit()
                    }else{
                        searchResultFragment.sendQuery(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
               /*     if (supportFragmentManager.findFragmentByTag(SearchResultFragment::class.java.simpleName)==null) {
                        supportFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.container, SearchResultFragment.newInstance(newText),SearchResultFragment::class.java.simpleName)
                            .commit()
                    }*/
                    return true
                }
            })

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.search_view_shape_bold,
                        null
                    )
                    findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
                        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.west_back))
                    }

                    if (supportFragmentManager.findFragmentByTag(SearchFragment::class.java.simpleName) == null) {
                        isClickable = true
                        supportFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(
                                R.id.container,
                                SearchFragment.newInstance(),
                                SearchFragment::class.java.simpleName
                            ).commit()
                    }
                }
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PagerCollectionFragment.newInstance())
                .commit()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        findViewById<TextView>(androidx.appcompat.R.id.search_src_text).text = ""
    }

    override fun onPause() {
        super.onPause()
        viewModel.flushSavedRequestFromMemoryToDisk()
    }
}