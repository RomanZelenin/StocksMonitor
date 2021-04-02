package com.romanzelenin.stocksmonitor

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.ExperimentalPagingApi
import com.romanzelenin.stocksmonitor.databinding.ActivityMainBinding
import com.romanzelenin.stocksmonitor.db.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainActivityViewModel(application, Repository(applicationContext)) as T
            }
        }
    }

    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)

        binding.appBarSearch.apply {
            findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
                setOnClickListener { onBackPressed() }
                isClickable = false
            }
            findViewById<View>(androidx.appcompat.R.id.search_plate).setBackgroundColor(Color.TRANSPARENT)
            findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn).setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.close_icon)
            )
            findViewById<TextView>(androidx.appcompat.R.id.search_src_text).apply {
                setHintTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(context, R.font.montserrat)
            }
            setOnQueryTextFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.search_view_shape_bold,
                        null
                    )
                    findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
                        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.west_back))
                        isClickable = true
                    }
                    if (navController.currentDestination?.id == R.id.pager_collection) {
                        navController.navigate(R.id.action_pager_collection_to_searchFragment)
                    }
                } else {
                    background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.search_view_shape,
                        null
                    )
                    /*     if (navController.currentDestination?.id == R.id.searchFragment){
                             navController.navigate(R.id.action_global_pager_collection)
                         }*/
                }
            }



            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                var queryJob: Job? = null
                override fun onQueryTextSubmit(query: String): Boolean {
                    queryJob?.cancel()
                    if (navController.currentDestination?.id != R.id.searchResultFragment) {
                        navController.navigate(R.id.action_searchFragment_to_searchResultFragment)
                    }
                    query.trim().let {
                        if (it != viewModel.searchQuery.value) {
                            viewModel.saveSearchRequest(it)
                            viewModel.searchQuery.postValue(it)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    queryJob?.cancel()
                    val query = newText.trim()
                    if (query.isNotBlank() && navController.currentDestination?.id != R.id.searchResultFragment) {
                        queryJob = lifecycleScope.launch {
                            delay(1000)
                            viewModel.searchQuery.postValue(query)
                            viewModel.saveSearchRequest(query)
                            navController.navigate(R.id.action_searchFragment_to_searchResultFragment)
                        }
                    } else if (query.isEmpty() && navController.currentDestination?.id == R.id.searchResultFragment) {
                        navController.navigate(R.id.action_searchResultFragment_to_searchFragment)
                    } else if (query.isNotBlank() && navController.currentDestination?.id == R.id.searchResultFragment) {
                        if (query != viewModel.searchQuery.value) {
                            queryJob = lifecycleScope.launch {
                                delay(1000)
                                viewModel.searchQuery.postValue(query)
                                viewModel.saveSearchRequest(query)
                            }
                        }
                    }
                    return true
                }
            })
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.pager_collection) {
                binding.appBarSearch.apply {
                    setQuery("", false)
                    clearFocus()
                }
                findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
                    isClickable = false
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_search_black_24dp
                        )
                    )
                }
            } else {
                findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.west_back
                        )
                    )
                    isClickable = true
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.flushSavedRequestFromMemoryToDisk()
    }
}