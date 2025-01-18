package com.example.genshinstatistics.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.genshinstatistics.R
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.model.Character
import com.example.genshinstatistics.model.HistoryItem

class HistoryItemAdapter(
    private val historyItemsList: ArrayList<HistoryItem>
) : RecyclerView.Adapter<HistoryItemAdapter.ReminderViewHolder>() {


    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ReminderViewHolder(view)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(@NonNull holder: ReminderViewHolder, position: Int) {
        val historyItem: HistoryItem = historyItemsList[position]
        val character: Character? = ArchiveCharacterData.ITEMS.find { ad -> ad.name == historyItem.name }
        if (character != null) {
            holder.nameTextView.text = historyItem.name
            holder.locationTextView.text = character.region?.name
            holder.birthDateTextView.text = character.birthdate
            holder.pullRateTextView.text = historyItem.pullRate.toString()
            holder.winDateTextView.text = historyItem.winDate

            character.icon?.let {
                holder.iconBgCardView.setBackgroundResource(it)
            }
            character.iconBgColor?.let {
                holder.iconBgCardView.setBackgroundResource(it)
            }
            character.element?.let {
                holder.signImageView.setBackgroundResource(it)
            }
            historyItem.pullRateColor?.let {
                holder.pullRateTextView.setTextColor(
                    it
                )
            }
        }
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        params.bottomMargin = if (position == itemCount - 1) 150 else 0
        holder.itemView.layoutParams = params

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
        val iconBgCardView: CardView = itemView.findViewById(R.id.color_card)
        val birthDateTextView: TextView = itemView.findViewById(R.id.birthdate_value)
        val locationTextView: TextView = itemView.findViewById(R.id.location_value)
        val pullRateTextView: TextView = itemView.findViewById(R.id.number_value)
        val winDateTextView: TextView = itemView.findViewById(R.id.win_date_value)
    }

}
