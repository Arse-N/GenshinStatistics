package com.example.genshinstatistics.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.genshinstatistics.R
import com.example.genshinstatistics.adapters.BannerSliderAdapter
import com.example.genshinstatistics.adapters.GoalItemAdapter
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.constants.ArchiveWeaponData
import com.example.genshinstatistics.databinding.FragmentHomeBinding
import com.example.genshinstatistics.enums.ItemType
import com.example.genshinstatistics.model.BannerData
import com.example.genshinstatistics.model.GoalItem
import com.example.genshinstatistics.util.BannerFetcher
import com.example.genshinstatistics.util.BaseUtil
import com.example.genshinstatistics.util.JsonUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bannerViewPager: ViewPager2
    private lateinit var dotIndicators: LinearLayout
    private val sliderHandler = Handler(Looper.getMainLooper())
    private var bannerFetcher = BannerFetcher()
    private lateinit var goalAdapter: GoalItemAdapter
    private lateinit var goalItemList: ArrayList<GoalItem>
    private lateinit var bannersData: List<BannerData>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        goalItemList = JsonUtil.readFromGoalJson(requireContext()) ?: ArrayList()
        setupRecyclerView(goalItemList)
        bannerViewPager = binding.bannerViewPager
        dotIndicators = binding.dotIndicators
        val addGoalButton: ImageView = binding.addButton
        addGoalButton.setOnClickListener(View.OnClickListener {
            showGoalItemDialog()
        })
        setupBannersData()
        return binding.root
    }

    private fun setupBannersData() {
        bannersData = JsonUtil.readFromBannersJson(requireContext()) ?: ArrayList()
        if (bannersData.isNotEmpty()) {
            val firstBanner = bannersData[0]
            val today = Date()
            val end = try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(firstBanner.end)
            } catch (e: ParseException) {
                null
            }

            if (end != null && today.after(end)) {
                bannerFetcher.fetchBanners { banners ->
                    JsonUtil.writeToBannerJson(requireContext(), banners)
                    bannersData = banners
                }
            }
        } else {
            bannerFetcher.fetchBanners { banners ->
                JsonUtil.writeToBannerJson(requireContext(), banners)
                bannersData = banners
            }
        }

        bannerViewPager.adapter = BannerSliderAdapter(bannersData)
        setupDotIndicators()
        bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)

                updateDotIndicators(position)
            }
        })
    }

    private fun setupRecyclerView(goalItemList: MutableList<GoalItem>) {
        goalAdapter = GoalItemAdapter(goalItemList) { position ->
            removeItem(position)
        }
        binding.goalItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = goalAdapter
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showGoalItemDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_goal_item_dialog, null)
        val nameSpinner: AutoCompleteTextView = dialogView.findViewById(R.id.item_selector)
        val ascTypeSpinner: Spinner = dialogView.findViewById(R.id.wish_type_selector)
        val errorText: TextView = dialogView.findViewById(R.id.item_selector_error)
        var chosenItemName = ""
        var chosenItemType = ItemType.CHARACTER
        var chosenAscType = ""

        setUpCharacterData(nameSpinner) { itemName ->
            if (itemName != null) {
                chosenItemName = itemName
                val foundItem = ArchiveWeaponData.Weapons.find { it.name == itemName }
                    ?: ArchiveCharacterData.Characthers.find { it.name == itemName }
                chosenItemType = foundItem?.type ?: return@setUpCharacterData

                setupAscData(ascTypeSpinner, chosenItemType) { itemRank ->
                    if (itemRank != null) {
                        chosenAscType = itemRank
                    }
                }
            }
        }



        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.setCanceledOnTouchOutside(false)
        dialogView.findViewById<View>(R.id.dialog_close).setOnClickListener {
            goalAdapter.updateList(goalItemList)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.dialog_done).setOnClickListener {
            var isValid = true

            if (chosenItemName.isNullOrEmpty()) {
                errorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.rarity_v5))
                isValid = false
            } else {
                errorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            if (isValid) {
                val goalItem = GoalItem(BaseUtil.generateCode(), chosenItemName, chosenAscType)
                addNewItem(goalItem)
                dialog.dismiss();
            }

        }

        dialog.show()
    }

    private fun setUpCharacterData(
        spinner: AutoCompleteTextView,
        onItemSelected: (String?) -> Unit
    ) {

        val sortedItems = (ArchiveCharacterData.Characthers + ArchiveWeaponData.Weapons)
            .map { it.name ?: "Unknown" }
            .sortedByDescending { it }

        val adapter = ArrayAdapter<String>(requireContext(), R.layout.custom_dropdown_item, sortedItems)

        spinner.setAdapter(adapter)
        spinner.threshold = 1
        spinner.setOnClickListener {
            spinner.showDropDown()
        }

        spinner.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            onItemSelected(selectedItem)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupAscData(
        rankSpinner: Spinner,
        type: ItemType,
        onItemSelected: (String?) -> Unit
    ) {
        val rankOptions = when (type) {
            ItemType.WEAPON -> listOf("R1", "R2", "R3", "R4", "R5")
            ItemType.CHARACTER -> listOf("C0", "C1", "C2", "C3", "C4", "C5", "C6")
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, rankOptions)
        rankSpinner.adapter = adapter

        rankSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedRank = rankOptions[position]
                onItemSelected(selectedRank)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addNewItem(goalItem: GoalItem) {
        goalItemList.add(goalItem)
        goalAdapter.notifyItemInserted(goalItemList.size - 1)
        JsonUtil.writeToGoalJson(requireContext(), goalItemList)
    }

    fun removeItem(position: Int) {
        goalItemList.removeAt(position)
        goalAdapter.notifyItemRemoved(position)
        goalAdapter.updateList(goalItemList)
        JsonUtil.writeToGoalJson(requireContext(), goalItemList)
    }

    private fun setupDotIndicators() {
        dotIndicators.removeAllViews()

        for (i in bannersData.indices) {
            val dot = View(context).apply {
                val width = resources.getDimensionPixelSize(R.dimen.dot_width)
                val height = resources.getDimensionPixelSize(R.dimen.dot_height)
                val params = LinearLayout.LayoutParams(width, height).apply {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.dot_margin)
                }
                layoutParams = params
                setBackgroundResource(R.drawable.ic_unselected_dot_bg)
            }

            dotIndicators.addView(dot)
        }
        updateDotIndicators(0)
    }


    private fun updateDotIndicators(position: Int) {
        for (i in 0 until dotIndicators.childCount) {
            val dot = dotIndicators.getChildAt(i)
            if (i == position) {
                dot.setBackgroundResource(R.drawable.ic_selected_dot_bg)
            } else {
                dot.setBackgroundResource(R.drawable.ic_unselected_dot_bg)
            }
        }
    }

    private val sliderRunnable = Runnable {
        val nextItem = (bannerViewPager.currentItem + 1) % bannersData.size
        bannerViewPager.setCurrentItem(nextItem, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
