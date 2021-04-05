package com.romanzelenin.stocksmonitor.ui.news

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.romanzelenin.stocksmonitor.R
import com.romanzelenin.stocksmonitor.model.CompanyNews
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import jp.wasabeef.picasso.transformations.ColorFilterTransformation


class NewsItemRecyclerViewAdapter(
    private val values: List<CompanyNews>
) : RecyclerView.Adapter<NewsItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_news_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.apply {
            headline.text = item.headline
            if (item.image.isNotEmpty()) {
                Picasso.get().load(item.image)
                    .resize(700, 400)
                    .error(R.drawable.news_placeholder)
                    .placeholder(R.drawable.news_placeholder)
                    .onlyScaleDown()
                    .transform(ColorFilterTransformation(itemView.context.getColor(R.color.black_transparent)))
                    .transform(BlurTransformation(itemView.context))
                    .into(image)
            }else{
                image.setImageDrawable(ContextCompat.getDrawable(image.context,R.drawable.news_placeholder))
            }
            source.text = item.source
            datetime.text = item.datetime
            summary.text = item.summary
            itemView.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                startActivity(it.context, browserIntent, null)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headline: TextView = view.findViewById(R.id.headline)
        val image: ImageView = view.findViewById(R.id.image)
        val source: TextView = view.findViewById(R.id.source)
        val datetime: TextView = view.findViewById(R.id.datetime)
        val summary: TextView = view.findViewById(R.id.summary)
    }
}