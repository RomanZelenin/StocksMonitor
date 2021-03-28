package com.romanzelenin.stocksmonitor

import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.romanzelenin.stocksmonitor.databinding.ActivityMainBinding
import com.romanzelenin.stocksmonitor.db.Repository
import com.romanzelenin.stocksmonitor.db.remotedata.FinService
import com.romanzelenin.stocksmonitor.ui.PagerCollectionFragment
import com.romanzelenin.stocksmonitor.ui.SearchFragment
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

        binding.appBarSearch.apply {
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
            }
            findViewById<TextView>(androidx.appcompat.R.id.search_src_text).apply {
                setHintTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(context, R.font.montserrat)
            }

            setOnQueryTextFocusChangeListener { searchBar, hasFocus ->
                if (hasFocus) {
                   /* supportFragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.container, SearchFragment.newInstance())
                        .commit()*/
                }
            }
        }


      if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, PagerCollectionFragment.newInstance())
                .commit()
        }

    }
}