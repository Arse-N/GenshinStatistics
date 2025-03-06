package com.example.genshinstatistics.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import coil.load
import coil.request.CachePolicy
import com.example.genshinstatistics.R
import com.example.genshinstatistics.dto.ItemCount
import com.example.genshinstatistics.enums.ItemType

class OwnedItemGridAdapter(
    private var items: List<ItemCount>
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.owned_item, parent, false)
        val ownedItem = items[position]

        val itemIcon: ImageView = view.findViewById(R.id.owned_item_img)
        val itemBg: FrameLayout = view.findViewById(R.id.color_card)
        val itemCount: TextView = view.findViewById(R.id.item_count)

        ownedItem.imageUrl?.let {
            itemIcon.load(it) {
                crossfade(true)
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }
        }
        ownedItem.itemBg?.let {
            itemBg.setBackgroundResource(it)
        }
        itemCount.text = if (ownedItem.type == ItemType.CHARACTER) {
            "C${ownedItem.count}"
        } else {
            "R${ownedItem.count}"
        }
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<ItemCount>) {
        items = newList
        notifyDataSetChanged()
    }
}
