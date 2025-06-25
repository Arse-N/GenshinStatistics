package com.example.genshinstatistics.ui.statistic

import android.R
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.genshinstatistics.adapters.OwnedItemGridAdapter
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.constants.ArchiveWeaponData
import com.example.genshinstatistics.databinding.FragmentStatisticBinding
import com.example.genshinstatistics.dto.ItemCount
import com.example.genshinstatistics.enums.*
import com.example.genshinstatistics.model.HistoryItem
import com.example.genshinstatistics.services.StatisticsService
import com.example.genshinstatistics.util.JsonUtil
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

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
    private lateinit var chartView: ConstraintLayout

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
        chartView = binding.chartsLayout
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
            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedStatisticType = statisticTypes[position].displayName
                when(selectedStatisticType){
                    StatisticType.WISH_STATISTICS.displayName -> showStatistics()
                    StatisticType.WISH_CHARTS.displayName -> showCharts()
                    StatisticType.OWNED_CHARACTERS.displayName -> showOwnedItems(ItemType.CHARACTER)
                    StatisticType.OWNED_WEAPON.displayName -> showOwnedItems(ItemType.WEAPON)
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun showOwnedItems(itemType: ItemType) {
        statisticsView.visibility = View.GONE
        chartView.visibility = View.GONE
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
        chartView.visibility = View.GONE
        statisticsView.visibility = View.VISIBLE
        var bannerType = WishType.CHARACTER_WISH
        val statisticsData = statisticsService.getBannerStatistics(bannerType, historyItemsList)

        binding.totalPullsValue.text = statisticsData["totalPulls"].toString()
        binding.pityCountValue.text = statisticsData["pityCount"].toString()
        binding.fifty50WinsValue.text = statisticsData["fifty50Wins"].toString()
        binding.fifty50LosesValue.text = statisticsData["fifty50Loses"].toString()
        binding.fifty50WinsStrikeValue.text = statisticsData["fifty50WinsRecordStrike"].toString()
        binding.fifty50LosesStrikeValue.text = statisticsData["fifty50LosesRecordStrike"].toString()
        binding.fifty50LosesWinsStrikeCurrentValue.text = statisticsData["currentStrike"].toString()
        val currentStrikeType = statisticsService.getLastPullWinRate(bannerType, historyItemsList)
        if (currentStrikeType == WinRateType.FIFTY_FIFTY_WIN.displayName) {
            binding.fifty50LosesWinsStrikeCurrentTitle.text = "Ongoing 50/50 wins strike:"
            binding.fifty50LosesWinsStrikeCurrentValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.holo_green_dark))
        } else {
            binding.fifty50LosesWinsStrikeCurrentTitle.text = "Ongoing 50/50 Loses strike:"
            binding.fifty50LosesWinsStrikeCurrentValue.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.holo_red_dark)
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCharts() {
        gridView.visibility = View.GONE
        statisticsView.visibility = View.GONE
        chartView.visibility = View.VISIBLE

        setupWinsPieChart()
        setupBigChart()

    }

    private fun setupWinsPieChart(){
        val winsPieChart: PieChart = binding.pieChart
        val pieChartData: List<PieEntry> = statisticsService.getPieChartData();
        val dataSet = PieDataSet(pieChartData, "")
        dataSet.colors = listOf(
            Color.parseColor("#454C5C"),  // gold
            Color.parseColor("#CEAB81"), // dark blue
        )
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 10f
        val data = PieData(dataSet)
        winsPieChart.data = data
        dataSet.setDrawValues(true)
        winsPieChart.setDrawEntryLabels(false)
        winsPieChart.description.isEnabled = false
        winsPieChart.centerText = ""
        winsPieChart.setEntryLabelColor(Color.BLACK)
        winsPieChart.extraBottomOffset = 6f
        winsPieChart.animateY(1000)
        winsPieChart.invalidate()
        winsPieChart.holeRadius = 50f
        winsPieChart.legend.setDrawInside(true)
//        val legend = winsPieChart.legend
//        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//        legend.orientation = Legend.LegendOrientation.VERTICAL
//        legend.setDrawInside(true)
//        legend.textSize = 11f
//        legend.form = Legend.LegendForm.SQUARE
//        legend.xEntrySpace = 5f
//        legend.yOffset = 10f
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupBigChart() {
        val bigChart: BarChart = binding.barChart
        val (bigChartData, xLabels) = statisticsService.getBigChartData(ChartFilteringType.WEEKLY)

        val dataSet = BarDataSet(bigChartData, "")
        dataSet.colors = listOf(
            Color.parseColor("#CEAB81"),
            Color.parseColor("#454C5C")
        )
        dataSet.setDrawValues(true)
        dataSet.valueTextColor = Color.RED
        dataSet.valueTextSize = 10f

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                val index = barEntry?.x?.toInt() ?: 0
                return historyItemsList.getOrNull(index)?.name ?: ""
            }
        }

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        bigChart.data = data
        bigChart.setFitBars(true)
        bigChart.description.isEnabled = false
        bigChart.extraBottomOffset = 6f
        bigChart.animateY(1000)
        bigChart.invalidate()
        bigChart.legend.isEnabled = false

        // 📅 Show winDate on X-axis
        val xAxis = bigChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}