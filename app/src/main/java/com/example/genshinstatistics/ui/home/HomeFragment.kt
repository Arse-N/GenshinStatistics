package com.example.genshinstatistics.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.genshinstatistics.R
import com.example.genshinstatistics.adapters.BannerSliderAdapter

class HomeFragment : Fragment() {
    private lateinit var bannerViewPager: ViewPager2
    private lateinit var dotIndicators: LinearLayout
    private val sliderHandler = Handler(Looper.getMainLooper())

    private val imageList = listOf(
        R.drawable.ic_anemo_bg,
        R.drawable.ic_geo_bg,
        R.drawable.ic_hydro_bg
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerViewPager = view.findViewById(R.id.bannerViewPager)
        dotIndicators = view.findViewById(R.id.dotIndicators)

        bannerViewPager.adapter = BannerSliderAdapter(imageList)

        // Set up dots for each page
        setupDotIndicators()

        bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)

                // Update dots when page changes
                updateDotIndicators(position)
            }
        })
    }

    private fun setupDotIndicators() {
        // Clear any existing dots
        dotIndicators.removeAllViews()

        // Create a dot for each image in the ViewPager
        for (i in imageList.indices) {
            val dot = View(context).apply {
                val width = resources.getDimensionPixelSize(R.dimen.dot_width) // 5dp
                val height = resources.getDimensionPixelSize(R.dimen.dot_height) // 8dp
                val params = LinearLayout.LayoutParams(width, height).apply {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.dot_margin) // 5dp spacing
                }
                layoutParams = params
                setBackgroundResource(R.drawable.ic_unselected_dot_bg) // Default unselected dot
            }

            dotIndicators.addView(dot)
        }

        // Set the first dot as selected
        updateDotIndicators(0)
    }


    private fun updateDotIndicators(position: Int) {
        for (i in 0 until dotIndicators.childCount) {
            val dot = dotIndicators.getChildAt(i)
            if (i == position) {
                dot.setBackgroundResource(R.drawable.ic_selected_dot_bg) // Change to selected drawable
            } else {
                dot.setBackgroundResource(R.drawable.ic_unselected_dot_bg) // Change to unselected drawable
            }
        }
    }

    private val sliderRunnable = Runnable {
        val nextItem = (bannerViewPager.currentItem + 1) % imageList.size
        bannerViewPager.setCurrentItem(nextItem, true)
    }
}
