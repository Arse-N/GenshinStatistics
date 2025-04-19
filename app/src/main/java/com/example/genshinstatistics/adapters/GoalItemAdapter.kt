package com.example.genshinstatistics.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.example.genshinstatistics.R
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.constants.ArchiveWeaponData
import com.example.genshinstatistics.enums.GoalItemStatus
import com.example.genshinstatistics.model.GoalItem
import com.example.genshinstatistics.model.Item

class GoalItemAdapter(
    private var goalItemsList: MutableList<GoalItem>,
    private val onItemRemove: (Int) -> Unit
) : RecyclerView.Adapter<GoalItemAdapter.GoalItemViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): GoalItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item, parent, false)
        return GoalItemViewHolder(view)
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(@NonNull holder: GoalItemViewHolder, position: Int) {
        val goalItem: GoalItem = goalItemsList[position]
        val items = (ArchiveCharacterData.Characthers + ArchiveWeaponData.Weapons)
        val item: Item? = items.find { it.name == goalItem.name }

        if (item != null) {
            with(holder) {
                nameTextView.text = goalItem.name
                (goalItem.ascension + (goalItem.goalCount - 1)).also { ascTextView.text = it }
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

                if(goalItem.status == GoalItemStatus.DONE){
                    (goalItem.ascension + (goalItem.obtained - 1)).also { ascTextView.text = it }
                    removeButton.setImageResource(R.drawable.ic_goal_done)
                    removeButton.isClickable = false
                    goalCountHeader.text = goalItem.obtainedDate
                    goalCountValue.visibility = View.GONE
                }else{
                    removeButton.setImageResource(R.drawable.ic_goal_remove)
                    removeButton.isClickable = true
                    goalCountValue.visibility = View.VISIBLE
                    goalCountHeader.text = "remaining: "
                    goalCountValue.text = goalItem.remaining.toString()
                    holder.removeButton.setOnClickListener {
                        onItemRemove(position)
                    }
                }
            }

        }


    }


    override fun getItemCount(): Int {
        return goalItemsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: MutableList<GoalItem>) {
        goalItemsList = newList
        notifyDataSetChanged()
    }

    class GoalItemViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name_value)
        val ascTextView: TextView = itemView.findViewById(R.id.asc_value)
        val iconImageView: ImageView = itemView.findViewById(R.id.item_img)
        val iconBgCardView: LinearLayout = itemView.findViewById(R.id.color_card)
        val goalCountHeader: TextView = itemView.findViewById(R.id.item_count_header)
        val goalCountValue: TextView = itemView.findViewById(R.id.item_count_value)
        val removeButton: ImageView = itemView.findViewById(R.id.remove_button)
    }

}
