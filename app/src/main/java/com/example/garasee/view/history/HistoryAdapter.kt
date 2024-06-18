package com.example.garasee.view.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.garasee.R
import com.example.garasee.data.api.ContentItem
import java.text.NumberFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoryAdapter(private val contentItem: List<ContentItem>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return HistoryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val content = contentItem[position]
        holder.contentBrand.text = content.brand
        holder.contentResult.text = if (content.isAcceptable) "Acceptable" else "Not Acceptable"
        val parsedDateTime = ZonedDateTime.parse(content.createdAt, DateTimeFormatter.ISO_DATE_TIME)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDateTime = parsedDateTime.format(outputFormatter)
        holder.contentTimestamp.text = formattedDateTime
        val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val formattedPrice = format.format(content.price.toDouble())
        holder.contentPrice.text = formattedPrice

    }

    override fun getItemCount(): Int = contentItem.size

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentBrand: TextView = view.findViewById(R.id.tv_brand)
        val contentPrice: TextView = view.findViewById(R.id.tv_price)
        val contentResult: TextView = view.findViewById(R.id.tv_result)
        val contentTimestamp: TextView = view.findViewById(R.id.tv_timestamp)
    }
}