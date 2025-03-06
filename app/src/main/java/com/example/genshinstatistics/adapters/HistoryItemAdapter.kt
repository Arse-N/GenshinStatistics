package com.example.genshinstatistics.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.example.genshinstatistics.R
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.constants.ArchiveWeaponData
import com.example.genshinstatistics.enums.ItemType
import com.example.genshinstatistics.model.HistoryItem
import com.example.genshinstatistics.model.Item

class HistoryItemAdapter(
    private val context: Context,
    private var historyItemsList: List<HistoryItem>,
    private var searchText: String = ""
) : RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder>() {


    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return HistoryItemViewHolder(view)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(@NonNull holder: HistoryItemViewHolder, position: Int) {
        val historyItem: HistoryItem = historyItemsList[position]
        val items = (ArchiveCharacterData.Characthers + ArchiveWeaponData.Weapons)
        val item: Item? = items.find { it.name == historyItem.name }

        if (item != null) {
            with(holder) {
                if (item.type == ItemType.WEAPON) {
                    locationLine.visibility = View.GONE
                    item.itemTypeIcon?.let {
                        birthDateIcon.setBackgroundResource(it)
                    }
                    nameTextView.setSingleLine(false)
                    birthDateTextView.text = item.weaponType?.displayName
                } else {
                    locationLine.visibility = View.VISIBLE
                    birthDateIcon.setBackgroundResource(R.drawable.ic_date)
                    nameTextView.setSingleLine(true)
                    birthDateTextView.text = item.birthdate
                }
                nameTextView.text = historyItem.name
                nameTextView.text = getHighlightedText(historyItem.name, searchText)
                locationTextView.text = item.region?.displayName

                wishRateTextView.text = historyItem.wishRate.toString()
                winDateTextView.text = historyItem.winDate
                winRateTextView.text = historyItem.winRate
                item.icon?.let {
                    iconImageView.load(it) {
                        crossfade(true)
                        memoryCachePolicy(CachePolicy.ENABLED)
                        diskCachePolicy(CachePolicy.ENABLED)
                    }
                }

                item.iconBgColor?.let {
                    iconBgCardView.setBackgroundResource(it)
                }

                item.itemTypeIcon?.let {
                    signImageView.setBackgroundResource(it)
                }

                historyItem.wishRateColor?.let {
                    wishRateTextView.setTextColor(it)
                }
            }
        }

        holder.itemView.layoutParams = (holder.itemView.layoutParams as RecyclerView.LayoutParams).apply {
            bottomMargin = if (position == itemCount - 1) 150 else 0
        }
    }


    override fun getItemCount(): Int {
        return historyItemsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<HistoryItem>) {
        historyItemsList = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSearchList(newList: List<HistoryItem>, query: String) {
        historyItemsList = newList
        searchText = query
        notifyDataSetChanged()
    }


    private fun getHighlightedText(fullText: String, searchText: String): SpannableString {
        val spannable = SpannableString(fullText)
        val startIndex = fullText.lowercase().indexOf(searchText.lowercase())

        if (startIndex >= 0) {
            val highlightColor = ContextCompat.getColor(context, R.color.light_gold)

            spannable.setSpan(
                BackgroundColorSpan(highlightColor),
                startIndex,
                startIndex + searchText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }

    class HistoryItemViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name_value)
        val signImageView: ImageView = itemView.findViewById(R.id.sign_icon)
        val iconImageView: ImageView = itemView.findViewById(R.id.item_img)
        val iconBgCardView: LinearLayout = itemView.findViewById(R.id.color_card)
        val birthDateTextView: TextView = itemView.findViewById(R.id.birthdate_value)
        val locationTextView: TextView = itemView.findViewById(R.id.location_value)
        val wishRateTextView: TextView = itemView.findViewById(R.id.wish_rate_value)
        val winRateTextView: TextView = itemView.findViewById(R.id.win_rate_value)
        val winDateTextView: TextView = itemView.findViewById(R.id.win_date_value)
        val locationLine: LinearLayout = itemView.findViewById(R.id.linearLayout3)
        val birthDateIcon: ImageView = itemView.findViewById(R.id.date_icon)
    }

}
