package com.romanzelenin.stocksmonitor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.romanzelenin.stocksmonitor.model.PopularRequest

class PopularReqAdapter(var dataSet: List<PopularRequest>) :
    RecyclerView.Adapter<PopularReqAdapter.PopularReqVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularReqVH {
        return PopularReqVH.create(parent)
    }

    override fun onBindViewHolder(holder: PopularReqVH, position: Int) {
        holder.apply {
            suggestion.text = dataSet[position].name
            suggestion.setOnClickListener {
                //Todo: add search
                Toast.makeText(it.context,suggestion.text, Toast.LENGTH_SHORT).show()
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