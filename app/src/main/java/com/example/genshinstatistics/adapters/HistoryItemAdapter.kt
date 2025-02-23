package com.example.genshinstatistics.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.example.genshinstatistics.R
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.model.Character
import com.example.genshinstatistics.model.HistoryItem

class HistoryItemAdapter(
    private val historyItemsList: List<HistoryItem>
) : RecyclerView.Adapter<HistoryItemAdapter.ReminderViewHolder>() {


    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ReminderViewHolder(view)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(@NonNull holder: ReminderViewHolder, position: Int) {
        val historyItem: HistoryItem = historyItemsList[position]
        val character: Character? = ArchiveCharacterData.ITEMS.find { it.name == historyItem.name }

        if (character != null) {
            with(holder) {
                nameTextView.text = historyItem.name
                locationTextView.text = character.region?.name
                birthDateTextView.text = character.birthdate
                wishRateTextView.text = historyItem.wishRate.toString()
                winDateTextView.text = historyItem.winDate
                winRateTextView.text = historyItem.winRate
                character.icon?.let {
                    iconImageView.load(it) {
                        crossfade(true)
                        memoryCachePolicy(CachePolicy.ENABLED)
                        diskCachePolicy(CachePolicy.ENABLED)
                    }
                }

                character.iconBgColor?.let {
                    iconBgCardView.setBackgroundResource(it)
                }

                character.element?.let {
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

    class ReminderViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerItem: LinearLayout = itemView.findViewById(R.id.recycler_item)
        val historyItem: ConstraintLayout = itemView.findViewById(R.id.history_item)
        val nameTextView: TextView = itemView.findViewById(R.id.name_value)
        val signImageView: ImageView = itemView.findViewById(R.id.sign_icon)
        val iconImageView: ImageView = itemView.findViewById(R.id.item_img)
        val iconBgCardView: LinearLayout = itemView.findViewById(R.id.color_card)
        val birthDateTextView: TextView = itemView.findViewById(R.id.birthdate_value)
        val locationTextView: TextView = itemView.findViewById(R.id.location_value)
        val wishRateTextView: TextView = itemView.findViewById(R.id.wish_rate_value)
        val winRateTextView: TextView = itemView.findViewById(R.id.win_rate_value)
        val winDateTextView: TextView = itemView.findViewById(R.id.win_date_value)
    }

}
