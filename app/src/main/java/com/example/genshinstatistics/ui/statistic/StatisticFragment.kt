package com.example.genshinstatistics.ui.statistic

import android.R
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.genshinstatistics.adapters.OwnedItemGridAdapter
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.constants.ArchiveWeaponData
import com.example.genshinstatistics.databinding.FragmentStatisticBinding
import com.example.genshinstatistics.dto.ItemCount
import com.example.genshinstatistics.enums.ItemType
import com.example.genshinstatistics.enums.StatisticType
import com.example.genshinstatistics.enums.WinRateType
import com.example.genshinstatistics.enums.WishType
import com.example.genshinstatistics.model.HistoryItem
import com.example.genshinstatistics.services.GoalItemService
import com.example.genshinstatistics.services.StatisticsService
import com.example.genshinstatistics.util.JsonUtil
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private lateinit var ownedItemGridAdapter: OwnedItemGridAdapter
    private lateinit var historyItemsList: ArrayList<HistoryItem>
    private lateinit var statisticsService: StatisticsService
    private lateinit var statisticTypeSelector: Spinner
    private lateinit var selectedStatisticType: String
    private lateinit var gridView: LinearLayout
    private lateinit var statisticsView: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val root: View = binding.root
        historyItemsList = JsonUtil.readFromJson(requireContext()) ?: ArrayList()
        statisticTypeSelector = binding.statisticTypeSelector
        gridView = binding.gridLayout
        statisticsView = binding.statisticsLayout
        statisticsService = StatisticsService(historyItemsList, requireContext())
        setupStatisticTypeSpinner(statisticTypeSelector)
        return root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupStatisticTypeSpinner(
        statisticTypeSpinner: Spinner
    ) {
        val statisticTypes = StatisticType.entries
        val statisticTypeNames = statisticTypes.map { it.displayName }

        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, statisticTypeNames)
        statisticTypeSpinner.adapter = adapter
        val statisticTypeLayout: LinearLayout = binding.statisticArrowDownLayout

        statisticTypeLayout.setOnClickListener {
            statisticTypeSpinner.performClick()
        }
        statisticTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedStatisticType = statisticTypes[position].displayName
                when(selectedStatisticType){
                    StatisticType.WISH_STATISTICS.displayName -> showStatistics()
                    StatisticType.OWNED_CHARACTERS.displayName -> showOwnedItems(ItemType.CHARACTER)
                    StatisticType.OWNED_WEAPON.displayName -> showOwnedItems(ItemType.WEAPON)
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun showOwnedItems(itemType: ItemType) {
        statisticsView.visibility = View.GONE
        gridView.visibility = View.VISIBLE

        val groupedItems = historyItemsList
            .groupingBy { it.name }
            .eachCount()
            .map { (name, count) ->
                val item = when (itemType) {
                    ItemType.CHARACTER -> ArchiveCharacterData.Characthers.firstOrNull { it.name == name }
                    ItemType.WEAPON -> ArchiveWeaponData.Weapons.firstOrNull { it.name == name }
                }
                item?.let { ItemCount(it.name, it.icon, it.iconBgColor, itemType, if (itemType == ItemType.WEAPON) count else count - 1) }
            }
            .filterNotNull()

        ownedItemGridAdapter = OwnedItemGridAdapter( groupedItems)
        binding.ownedItems.apply {
            adapter = ownedItemGridAdapter
        }
    }

    private fun showStatistics() {
        gridView.visibility = View.GONE
        statisticsView.visibility = View.VISIBLE

        val primogems: TextView = binding.primogemsValue
        val wishPulls: TextView = binding.wishPullsValue
        val standardPulls: TextView = binding.standardPullsValue

        val (standardSum, otherWishSum, primogemsSum) = historyItemsList.fold(Triple(0, 0, 0)) { sums, item ->
            Triple(
                if (item.wishType == WishType.STANDARD_WISH.displayName) sums.first + item.wishRate!! else sums.first,
                if (item.wishType != WishType.STANDARD_WISH.displayName) sums.second + item.wishRate!! else sums.second,
                if (item.wishType != WishType.STANDARD_WISH.displayName) sums.third + (item.wishRate?.times(160)!!) else sums.third
            )
        }
        standardPulls.text = standardSum.toString()
        wishPulls.text = otherWishSum.toString()
        primogems.text = primogemsSum.toString()
        setupWinsPieChart()

    }

    private fun setupWinsPieChart(){
        val winsPieChart: PieChart = binding.pieChart
        val pieChartData: List<PieEntry> = statisticsService.getPieChartData();
        val dataSet = PieDataSet(pieChartData, "")
        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#F44336")  // Red
        )
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 12f
        val data = PieData(dataSet)
        winsPieChart.data = data
        dataSet.setDrawValues(true)
        winsPieChart.setDrawEntryLabels(false)
        winsPieChart.description.isEnabled = false
        winsPieChart.centerText = ""
        winsPieChart.setEntryLabelColor(Color.BLACK)
        winsPieChart.extraBottomOffset = 10f

        winsPieChart.animateY(1000)
        winsPieChart.invalidate()
        winsPieChart.holeRadius = 20f

        val legend = winsPieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(true)
        legend.textSize = 11f
        legend.form = Legend.LegendForm.SQUARE
        legend.xEntrySpace = 5f
        legend.yOffset = 10f
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}