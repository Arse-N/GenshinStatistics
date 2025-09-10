package com.example.genshinstatistics.ui.statistic

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.genshinstatistics.R
import com.example.genshinstatistics.adapters.OwnedItemGridAdapter
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.constants.ArchiveWeaponData
import com.example.genshinstatistics.databinding.FragmentStatisticBinding
import com.example.genshinstatistics.dto.ItemCount
import com.example.genshinstatistics.enums.*
import com.example.genshinstatistics.model.BannerPity
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
    private var selectedWishType: WishType = WishType.CHARACTER_WISH
    private val rateValueMap: MutableMap<String, Int> = mutableMapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        historyItemsList = JsonUtil.readFromJson(requireContext()) ?: ArrayList()
        statisticTypeSelector = binding.statisticTypeSelector
        gridView = binding.gridLayout
        statisticsView = binding.statisticsLayout
        chartView = binding.chartsLayout
        statisticsService = StatisticsService(historyItemsList, requireContext())

        val savedPity = rateValueMap[selectedWishType.displayName] ?: loadPityCount(selectedWishType)
        rateValueMap[selectedWishType.displayName] = savedPity

        setupStatisticTypeSpinner(statisticTypeSelector)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupStatisticTypeSpinner(statisticTypeSpinner: Spinner) {
        val statisticTypes = StatisticType.entries
        val statisticTypeNames = statisticTypes.map { it.displayName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statisticTypeNames)
        statisticTypeSpinner.adapter = adapter
        val statisticTypeLayout: LinearLayout = binding.statisticArrowDownLayout

        statisticTypeLayout.setOnClickListener { statisticTypeSpinner.performClick() }
        statisticTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedStatisticType = statisticTypes[position].displayName
                when (selectedStatisticType) {
                    StatisticType.WISH_STATISTICS.displayName -> {
                        showStatistics()
                        setupToggleButtons()
                    }
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
            }.filterNotNull()

        ownedItemGridAdapter = OwnedItemGridAdapter(groupedItems)
        binding.ownedItems.adapter = ownedItemGridAdapter
    }

    private fun showStatistics() {
        gridView.visibility = View.GONE
        chartView.visibility = View.GONE
        statisticsView.visibility = View.VISIBLE

        // Always load pity from JSON
        val savedPity = JsonUtil.readPityJson(requireContext())
            ?.firstOrNull { it.type == selectedWishType.displayName }
            ?.pityCount ?: 0

        // Update local map so dialog shows correct value
        rateValueMap[selectedWishType.displayName] = savedPity

        val statisticsData =
            statisticsService.getBannerStatistics(selectedWishType.displayName, historyItemsList)

        binding.totalPullsValue.text = statisticsData.totalPulls.toString()
        binding.pityCountValue.text = savedPity.toString()
        binding.fifty50WinsValue.text = statisticsData.fifty50Wins.toString()
        binding.fifty50LosesValue.text = statisticsData.fifty50Loses.toString()
        binding.fifty50WinsStrikeValue.text = statisticsData.fifty50WinsRecordStrike.toString()
        binding.fifty50LosesStrikeValue.text = statisticsData.fifty50LosesRecordStrike.toString()
        binding.fifty50LosesWinsStrikeCurrentValue.text = statisticsData.currentStrike.toString()

        if (selectedWishType == WishType.STANDARD_WISH) {
            binding.primogemIcon.setImageResource(R.drawable.ic_standart_pull)
            binding.wishPullsIcon.setImageResource(R.drawable.ic_standart_pull)
        } else {
            binding.primogemIcon.setImageResource(R.drawable.ic_wish_pull)
            binding.wishPullsIcon.setImageResource(R.drawable.ic_wish_pull)
        }

        binding.pityCount.setOnClickListener { showPityCountDialog() }

        val currentStrikeType =
            statisticsService.getLastPullWinRate(selectedWishType.displayName, historyItemsList)
        if (currentStrikeType == WinRateType.FIFTY_FIFTY_WIN.displayName) {
            binding.fifty50LosesWinsStrikeCurrentTitle.text = "Ongoing 50/50 wins strike:"
            binding.fifty50LosesWinsStrikeCurrentValue.setTypeface(null, Typeface.BOLD)
        } else {
            binding.fifty50LosesWinsStrikeCurrentTitle.text = "Ongoing 50/50 Loses strike:"
            binding.fifty50LosesWinsStrikeCurrentValue.setTypeface(null, Typeface.ITALIC)
        }
    }


    private fun onRateChanged(newValue: Int) {
        rateValueMap[selectedWishType.displayName] = newValue
    }

    private fun setupToggleButtons() {
        val characterButton = binding.character
        val standardButton = binding.standard
        val chronicalButton = binding.chronical
        val weaponButton = binding.weapon
        val buttonMap = mapOf(
            characterButton to WishType.CHARACTER_WISH,
            weaponButton to WishType.WEAPON_WISH,
            standardButton to WishType.STANDARD_WISH,
            chronicalButton to WishType.CHRONICAL_WISH
        )
        val buttons = buttonMap.keys

        fun applyTextStyle(selectedButton: Button) {
            buttons.forEach { it.isEnabled = true; it.setTypeface(null, Typeface.NORMAL) }
            selectedButton.isEnabled = false
            selectedButton.setTypeface(null, Typeface.BOLD)
        }

        applyTextStyle(characterButton)
        selectedWishType = WishType.CHARACTER_WISH

        for ((button, wishType) in buttonMap) {
            button.setOnClickListener { applyTextStyle(button); selectedWishType = wishType; showStatistics() }
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

    private fun setupWinsPieChart() {
        val winsPieChart: PieChart = binding.pieChart
        val pieChartData: List<PieEntry> = statisticsService.getPieChartData()
        val dataSet = PieDataSet(pieChartData, "")
        dataSet.colors = listOf(Color.parseColor("#454C5C"), Color.parseColor("#CEAB81"))
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupBigChart() {
        val bigChart: BarChart = binding.barChart
        val (bigChartData, xLabels) = statisticsService.getBigChartData(ChartFilteringType.WEEKLY)
        val dataSet = BarDataSet(bigChartData, "")
        dataSet.colors = listOf(Color.parseColor("#CEAB81"), Color.parseColor("#454C5C"))
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

        val xAxis = bigChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
    }

    private fun showPityCountDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_wish_count_dialog, null)
        val pityCount: NumberPicker = dialogView.findViewById(R.id.pity_count_selector)
        pityCount.minValue = 0
        pityCount.maxValue = 90

        pityCount.value = rateValueMap[selectedWishType.displayName] ?: 0
        onRateChanged(pityCount.value)
        pityCount.setOnValueChangedListener { _, _, newVal -> onRateChanged(newVal) }

        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        dialogView.findViewById<ImageButton>(R.id.dialog_close)?.setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<ImageButton>(R.id.dialog_done)?.setOnClickListener {
            val pity = rateValueMap[selectedWishType.displayName] ?: 0
            savePityCount(selectedWishType.displayName, pity)
            showStatistics()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadPityCount(wishType: WishType): Int {
        val savedList = JsonUtil.readPityJson(requireContext()) ?: return 0
        return savedList.firstOrNull { it.type == wishType.displayName }?.pityCount ?: 0
    }

    fun savePityCount(type: String, pityCount: Int) {
        val existingList = JsonUtil.readPityJson(requireContext())?.toMutableList() ?: mutableListOf()

        val index = existingList.indexOfFirst { it.type == type }
        if (index >= 0) {
            existingList[index] = BannerPity(type, pityCount) // update
        } else {
            existingList.add(BannerPity(type, pityCount)) // insert new
        }

        JsonUtil.writePityJson(requireContext(), ArrayList(existingList))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
