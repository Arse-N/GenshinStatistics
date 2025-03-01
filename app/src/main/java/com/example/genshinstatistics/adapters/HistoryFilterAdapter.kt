package com.example.genshinstatistics.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.genshinstatistics.R

class HistoryFilterAdapter(
    private val filters: List<String>,
    private val onItemClick: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<HistoryFilterAdapter.ViewHolder>() {

    private var selectedIndex = -1
    private var isAscending = true

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFilter: TextView = view.findViewById(R.id.tvFilter)
        val ivArrow: ImageView = view.findViewById(R.id.ivArrow)

        init {
            view.setOnClickListener {
                if (adapterPosition == selectedIndex) {
                    isAscending = !isAscending
                } else {
                    selectedIndex = adapterPosition
                    isAscending = true
                }
                onItemClick(selectedIndex, isAscending)
                notifyDataSetChanged()
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

        if (position == selectedIndex) {
            holder.ivArrow.visibility = View.VISIBLE
            holder.ivArrow.setImageResource(
                if (isAscending) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            )
        } else {
            holder.ivArrow.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = filters.size
}
