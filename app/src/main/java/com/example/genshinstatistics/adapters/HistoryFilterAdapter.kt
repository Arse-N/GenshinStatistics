package com.example.genshinstatistics.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.genshinstatistics.R

class HistoryFilterAdapter(
    private val filters: List<String>
) : RecyclerView.Adapter<HistoryFilterAdapter.ViewHolder>() {

    private var selectedIndex = -1
    private var isAscending = true

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFilter: TextView = view.findViewById(R.id.filter_item_text)
        val ivArrow: ImageView = view.findViewById(R.id.ADArrow)

        init {
            view.setOnClickListener {
                if (adapterPosition == selectedIndex) {
                    isAscending = !isAscending
                } else {
                    selectedIndex = adapterPosition
                    isAscending = true
                }
                notifyDataSetChanged() // Refresh UI
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.filter_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvFilter.text = filters[position]
        holder.ivArrow.visibility = if (position == selectedIndex) View.VISIBLE else View.GONE
        holder.ivArrow.setImageResource(
            if (isAscending) R.drawable.ic_long_arrow_up else R.drawable.ic_long_arrow_down
        )
    }

    override fun getItemCount(): Int = filters.size

    fun getSelectedFilter(): Pair<String, Boolean> {
        return if (selectedIndex in filters.indices) {
            filters[selectedIndex] to isAscending
        } else {
            filters[0] to true  // Default to first option in ascending order
        }
    }
}
