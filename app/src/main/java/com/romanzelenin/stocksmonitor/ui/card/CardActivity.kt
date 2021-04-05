package com.romanzelenin.stocksmonitor.ui.card

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.romanzelenin.stocksmonitor.R
import com.romanzelenin.stocksmonitor.databinding.ActivityCardBinding

class CardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.west_back)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setDisplayShowCustomEnabled(true)
            setCustomView(R.layout.custom_toolbar)
        }
        binding.toolbar.apply {
            findViewById<TextView>(R.id.ticker_name).text = intent.getStringExtra("ticker_name")
            findViewById<TextView>(R.id.short_name).text = intent.getStringExtra("short_name")

            if (intent.getBooleanExtra("favourite", false)) {
                findViewById<ImageButton>(R.id.star_btn).setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        android.R.drawable.btn_star_big_on,
                        null
                    )
                )
            } else {
                findViewById<ImageButton>(R.id.star_btn).setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        android.R.drawable.btn_star_big_off,
                        null
                    )
                )
            }
        }


        binding.apply {
            pager.adapter = CardPagerAdapter(this@CardActivity)
            TabLayoutMediator(tabLayout, pager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Chart"
                    }
                    1 -> {
                        tab.text = "Summary"
                    }
                    else -> {
                        tab.text = "News"
                    }
                }

            }.attach()
        }

    }
}