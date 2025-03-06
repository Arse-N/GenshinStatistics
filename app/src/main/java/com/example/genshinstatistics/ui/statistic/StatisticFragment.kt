package com.example.genshinstatistics.ui.statistic

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.genshinstatistics.adapters.HistoryItemAdapter
import com.example.genshinstatistics.adapters.OwnedItemGridAdapter
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.constants.ArchiveWeaponData
import com.example.genshinstatistics.databinding.FragmentStatisticBinding
import com.example.genshinstatistics.dto.ItemCount
import com.example.genshinstatistics.enums.ItemType
import com.example.genshinstatistics.enums.SortType
import com.example.genshinstatistics.enums.StatisticType
import com.example.genshinstatistics.enums.WishType
import com.example.genshinstatistics.model.HistoryItem
import com.example.genshinstatistics.util.JsonUtil
import com.example.genshinstatistics.util.SorterUtil
import java.util.ArrayList

class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private lateinit var ownedItemGridAdapter: OwnedItemGridAdapter
    private lateinit var historyItemsList: ArrayList<HistoryItem>
    private lateinit var filteredHistoryItemsList: ArrayList<HistoryItem>
    private lateinit var statisticTypeSelector: Spinner
    private lateinit var selectedStatisticType: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val root: View = binding.root
        historyItemsList = JsonUtil.readFromJson(requireContext()) ?: ArrayList()
        statisticTypeSelector = binding.statisticTypeSelector
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
                    StatisticType.OWNED_CHARACTERS.displayName -> showOwnedItems(ItemType.CHARACTER)
                    StatisticType.OWNED_WEAPON.displayName -> showOwnedItems(ItemType.WEAPON)
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun showOwnedItems(itemType: ItemType) {
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




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}