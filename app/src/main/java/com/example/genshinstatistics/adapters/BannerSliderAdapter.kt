package com.example.genshinstatistics.adapters

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class BannerSliderAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<BannerSliderAdapter.ViewHolder>() {

    inner class ViewHolder(view: ImageView) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as ImageView).setImageResource(images[position])
    }

    override fun getItemCount(): Int = images.size
}
