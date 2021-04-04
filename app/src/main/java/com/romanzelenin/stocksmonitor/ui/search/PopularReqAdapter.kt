package com.romanzelenin.stocksmonitor.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.romanzelenin.stocksmonitor.R

class PopularReqAdapter(var dataSet: List<String>, private val searchView: SearchView) :
    RecyclerView.Adapter<PopularReqAdapter.PopularReqVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularReqVH {
        return PopularReqVH.create(parent)
    }

    override fun onBindViewHolder(holder: PopularReqVH, position: Int) {
        holder.apply {
            suggestion.text = dataSet[position]
            suggestion.setOnClickListener {
                searchView.setQuery(dataSet[position],true)
            }
        }
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    class PopularReqVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val suggestion: TextView = itemView.findViewById(R.id.bubble)

        companion object {
            fun create(parent: ViewGroup): PopularReqVH {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.suggestion, parent, false)
                return PopularReqVH(view)
            }
        }
    }
}