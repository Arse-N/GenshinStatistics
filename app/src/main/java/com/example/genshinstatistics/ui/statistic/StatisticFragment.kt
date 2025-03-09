package com.example.genshinstatistics.ui.statistic

import android.R
import android.annotation.SuppressLint
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
import com.example.genshinstatistics.util.JsonUtil

class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private lateinit var ownedItemGridAdapter: OwnedItemGridAdapter
    private lateinit var historyItemsList: ArrayList<HistoryItem>
    private lateinit var filteredHistoryItemsList: ArrayList<HistoryItem>
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
        val fifty50Wins: TextView = binding.fifty50WinsValue
        val fifty50WinsStrike: TextView = binding.fifty50WinsStrikeValue
        val fifty50LoseStrike: TextView = binding.fifty50LoseStrikeValue

        val (standardSum, otherWishSum, primogemsSum) = historyItemsList.fold(Triple(0, 0, 0)) { sums, item ->
            Triple(
                if (item.wishType == WishType.STANDARD_WISH.displayName) sums.first + item.wishRate!! else sums.first,
                if (item.wishType != WishType.STANDARD_WISH.displayName) sums.second + item.wishRate!! else sums.second,
                if (item.wishType != WishType.STANDARD_WISH.displayName) sums.third + (item.wishRate?.times(160)!!) else sums.third
            )
        }

        val (win50Count, win50Streak, lose50Streak) = historyItemsList.fold(Triple(0, 0, 0)) { streaks, item ->
            Triple(
                if (item.winRate == WinRateType.FIFTY_FIFTY_WIN.displayName) streaks.first + 1 else streaks.first,
                if (item.winRate == WinRateType.FIFTY_FIFTY_WIN.displayName) streaks.second + 1 else 0,
                if (item.winRate == WinRateType.FIFTY_FIFTY_LOSE.displayName) streaks.third + 1 else 0
            )
        }

//
//        // Cache values
//        savePullRateSum(
//            requireContext(),
//            standardSum,
//            otherWishSum,
//            primogemsSum,
//            win50Count,
//            win50Streak,
//            lose50Streak
//        )

        standardPulls.text = standardSum.toString()
        wishPulls.text = otherWishSum.toString()
        primogems.text = primogemsSum.toString()
        fifty50Wins.text = win50Count.toString()
        fifty50WinsStrike.text = win50Streak.toString()
        fifty50LoseStrike.text = lose50Streak.toString()
    }










    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}