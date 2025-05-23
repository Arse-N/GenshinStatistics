package com.example.genshinstatistics.ui.history

import ItemSwiper
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.genshinstatistics.R
import com.example.genshinstatistics.adapters.HistoryFilterAdapter
import com.example.genshinstatistics.adapters.HistoryItemAdapter
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.constants.ArchiveWeaponData
import com.example.genshinstatistics.databinding.FragmentHistoryBinding
import com.example.genshinstatistics.enums.GoalItemStatus
import com.example.genshinstatistics.enums.SortType
import com.example.genshinstatistics.enums.WinRateType
import com.example.genshinstatistics.enums.WishType
import com.example.genshinstatistics.model.GoalItem
import com.example.genshinstatistics.model.HistoryItem
import com.example.genshinstatistics.services.GoalItemService
import com.example.genshinstatistics.util.BaseUtil
import com.example.genshinstatistics.util.JsonUtil
import com.example.genshinstatistics.util.SorterUtil
import java.util.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: HistoryItemAdapter
    private lateinit var historyItemsList: ArrayList<HistoryItem>
    private lateinit var filteredHistoryItemsList: ArrayList<HistoryItem>
    private lateinit var searchedHistoryItemsList: ArrayList<HistoryItem>
    private lateinit var goalItemsService: GoalItemService
    private lateinit var goalItemList: ArrayList<GoalItem>
    private lateinit var selectedWishType: String
    private lateinit var historySearchBar: SearchView
    private var popupWindow: PopupWindow? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        historyItemsList = JsonUtil.readFromJson(requireContext()) ?: ArrayList()
        goalItemList = JsonUtil.readFromGoalJson(requireContext()) ?: ArrayList()
        goalItemsService = GoalItemService(historyItemsList, goalItemList, requireContext())
        historySearchBar = binding.historySearchBar
        historySearchBar.clearFocus()
        filteredHistoryItemsList =
            SorterUtil.sortAndFilter(historyItemsList, SortType.WISH_TYPE, false, WishType.CHARACTER_WISH.displayName)
        setupRecyclerView(filteredHistoryItemsList)
        setupWishTypeSpinner(binding.wishTypeSelector);
        setUpHistorySearchBar(historySearchBar)
        val itemSwiper = object : ItemSwiper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        showRemoveItemDialog(
                            viewHolder.adapterPosition
                        )
                    }

                    ItemTouchHelper.RIGHT -> {
                        val historyItem: HistoryItem = filteredHistoryItemsList.get(viewHolder.adapterPosition)
                        showItemDialog(
                            historyItem,
                            viewHolder.adapterPosition
                        )
                    }

                }
                historyAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }
        }

        binding.addButton.setOnClickListener {
            showItemDialog(
                HistoryItem(
                    id = BaseUtil.generateCode(),
                ),
                null
            )
        }

        binding.filterButton.setOnClickListener {
            showPopup()
        }
        val itemTouchHelper = ItemTouchHelper(itemSwiper)
        itemTouchHelper.attachToRecyclerView(binding.historyItems)

        return binding.root
    }

    private fun setupRecyclerView(historyItemsList: List<HistoryItem>) {
        historyAdapter = HistoryItemAdapter(requireContext(), historyItemsList)

        binding.historyItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupWishTypeSpinner(
        wishTypeSpinner: Spinner
    ) {
        val wishTypes = WishType.entries
        val wishNames = wishTypes.map { it.displayName }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, wishNames)
        wishTypeSpinner.adapter = adapter

        val arrowButton = view?.findViewById<ImageButton>(R.id.wish_type_arrow)

        arrowButton?.setOnClickListener {
            wishTypeSpinner.performClick()
        }

        wishTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedWishType = wishTypes[position].displayName

                filteredHistoryItemsList =
                    SorterUtil.sortAndFilter(historyItemsList, SortType.WISH_TYPE, false, selectedWishType)
                searchedHistoryItemsList = ArrayList(filteredHistoryItemsList)
                setupRecyclerView(filteredHistoryItemsList)
                historyAdapter.updateList(filteredHistoryItemsList)
                historySearchBar.setQuery("", false)

                arrowButton?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showItemDialog(
        historyItem: HistoryItem,
        position:Int?
    ) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_history_item_dialog, null)
        val nameSpinner: AutoCompleteTextView = dialogView.findViewById(R.id.item_selector)
        val wishTypeSpinner: Spinner = dialogView.findViewById(R.id.wish_type_selector)
        val winRateSpinner: Spinner = dialogView.findViewById(R.id.win_rate_selector)
        val errorText: TextView = dialogView.findViewById(R.id.item_selector_error)
        var chosenItem: String = historyItem.name
        var chosenDate: String? = historyItem.winDate
        var chosenWishRate: Int? = historyItem.wishRate
        var chosenWinRate: String? = historyItem.winRate
        var chosenWishType: String? = historyItem.wishType

        setUpCharacterData(nameSpinner, chosenItem) { item ->
            if (item != null) {
                chosenItem = item
            }
        }
        setUpDatePicker(dialogView, chosenDate) { date -> chosenDate = date }
        setUpWishRateData(dialogView, { rate -> chosenWishRate = rate }, chosenWishRate)
        setUpWinRateData(winRateSpinner, chosenWinRate) { selected ->
            chosenWinRate = selected
        }
        setUpWishTypeData(wishTypeSpinner, chosenWishType) { selected ->
            chosenWishType = selected
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.setCanceledOnTouchOutside(false)
        dialogView.findViewById<View>(R.id.dialog_close).setOnClickListener {
            historyAdapter.updateList(filteredHistoryItemsList)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.dialog_done).setOnClickListener {
            var isValid = true

            if (chosenItem.isNullOrEmpty()) {
                errorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.rarity_v5))
                isValid = false
            } else {
                errorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            if (chosenDate.isNullOrEmpty()) {
                chosenDate = BaseUtil.getFormattedDate()
            }
            if (isValid) {
                historyItem.name = chosenItem
                historyItem.winDate = chosenDate
                historyItem.wishRate = chosenWishRate
                historyItem.winRate = chosenWinRate
                historyItem.wishRateColor = chosenWishRate?.let { it1 -> BaseUtil.chooseColor(requireContext(), it1) }
                historyItem.winDate = chosenDate
                historyItem.wishType = chosenWishType
                if (position == null) {
                    addNewItem(historyItem)
                    goalItemsService.updateItem(historyItem.name)
                } else {
                    updateItem(position, historyItem)
                }
                dialog.dismiss();
            }

        }

        dialog.show()
    }

    private fun setUpHistorySearchBar(historySearchBar: SearchView){
        historySearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterList(query: String?) {
        searchedHistoryItemsList = if (!query.isNullOrEmpty()) {
            ArrayList(filteredHistoryItemsList.filter { it.name.contains(query, ignoreCase = true)  })
        } else {
            ArrayList(filteredHistoryItemsList)
        }
        historyAdapter.updateSearchList(searchedHistoryItemsList, query ?: "")
    }


    private fun setUpCharacterData(
        spinner: AutoCompleteTextView,
        selectedItem: String?,
        onItemSelected: (String?) -> Unit
    ) {

        val sortedItems = (ArchiveCharacterData.Characthers + ArchiveWeaponData.Weapons)
            .map { it.name ?: "Unknown" }
            .sortedByDescending { it }

        val adapter = ArrayAdapter<String>(requireContext(), R.layout.custom_dropdown_item, sortedItems)

        spinner.setAdapter(adapter)
        spinner.threshold = 1
        spinner.setText(selectedItem)
        spinner.setOnClickListener {
            spinner.showDropDown()
        }

        spinner.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            onItemSelected(selectedItem)
        }
    }

    private fun setUpWishTypeData(
        spinner: Spinner,
        selectedItem: String?,
        onItemSelected: (String?) -> Unit
    ) {

        val displayNames =  WishType.entries.map {
            it.displayName
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.custom_dropdown_item, displayNames)
        spinner.adapter = adapter

        selectedItem?.let {
            val index = displayNames.indexOf(it)
            if (index != -1) {
                spinner.setSelection(index)
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = displayNames[position]
                onItemSelected(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                val selectedItem = displayNames[1]
                onItemSelected(selectedItem)
            }
        }
    }

    private fun setUpWinRateData(
        spinner: Spinner,
        selectedItem: String?,
        onItemSelected: (String?) -> Unit
    ) {

        val displayNames =  WinRateType.entries.map {
            it.displayName
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.custom_dropdown_item, displayNames)
        spinner.adapter = adapter

        selectedItem?.let {
            val index = displayNames.indexOf(it)
            if (index != -1) {
                spinner.setSelection(index)
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = displayNames[position]
                onItemSelected(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                val selectedItem = displayNames[1]
                onItemSelected(selectedItem)
            }
        }
    }





    @SuppressLint("ClickableViewAccessibility")
    private fun setUpWishRateData(view: View, onRateChanged: (Int) -> Unit, rateValue: Int?) {
        val wishRatePicker: NumberPicker = view.findViewById(R.id.wish_rate_selector)
        wishRatePicker.minValue = 0
        wishRatePicker.maxValue = 90

        wishRatePicker.value = rateValue ?: 0
        onRateChanged(wishRatePicker.value)

        wishRatePicker.setOnValueChangedListener { _, _, newVal ->
            onRateChanged(newVal)
        }
    }

    private fun setUpDatePicker(view: View, selectedDate: String?, onDateSelected: (String?) -> Unit) {
        val dateInput: EditText = view.findViewById(R.id.win_date_selector)
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        if (!selectedDate.isNullOrEmpty()) {
            dateInput.setText(selectedDate)
        } else {
            val todayDate = "$currentDay/${currentMonth + 1}/$currentYear"
            dateInput.setText(todayDate)
        }

        val datePickerDialog = DatePickerDialog(
            view.context,
            R.style.CustomDatePicker,
            { _, selectedYear, selectedMonth, selectedDay ->
                val newSelectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateInput.setText(newSelectedDate)
                onDateSelected(newSelectedDate)
            },
            currentYear, currentMonth, currentDay
        )

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        dateInput.setOnClickListener {
            datePickerDialog.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addNewItem(historyItem: HistoryItem) {
        historyItemsList.add(historyItem)
        val newFilteredList = SorterUtil.sortAndFilter(historyItemsList, SortType.WISH_TYPE, false, selectedWishType)
        filteredHistoryItemsList.clear()
        filteredHistoryItemsList.addAll(newFilteredList)
        JsonUtil.writeToJson(requireContext(), historyItemsList)
        if (filteredHistoryItemsList.size > 1) {
            historyAdapter.notifyItemChanged(filteredHistoryItemsList.size - 2)
        }
        historyAdapter.updateList(filteredHistoryItemsList)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateItem(position: Int, newHistoryItem: HistoryItem) {
        val originalItem = filteredHistoryItemsList.getOrNull(position) ?: return
        val newPosition = historyItemsList.indexOf(originalItem)

        if (newPosition != -1) {
            historyItemsList[newPosition] = newHistoryItem
            val newFilteredList =
                SorterUtil.sortAndFilter(historyItemsList, SortType.WISH_TYPE, false, selectedWishType)
            val updatedIndex = newFilteredList.indexOf(newHistoryItem)
            filteredHistoryItemsList.clear()
            filteredHistoryItemsList.addAll(newFilteredList)

            if (updatedIndex != -1) {
                historyAdapter.notifyItemChanged(updatedIndex)
            } else {
                historyAdapter.updateList(filteredHistoryItemsList)
            }

            JsonUtil.writeToJson(requireContext(), historyItemsList)
        }
    }


    private fun removeItem(
        position: Int
    ) {
        val newPosition = historyItemsList.indexOf(filteredHistoryItemsList[position])
        if (newPosition != -1) {
            goalItemsService.removeItem(historyItemsList[newPosition].name)
            historyItemsList.removeAt(newPosition)
            filteredHistoryItemsList.removeAt(position)
            historyAdapter.notifyItemRemoved(position)
            JsonUtil.writeToJson(requireContext(), historyItemsList)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPopup() {
        val filterOptions = SortType.entries.drop(1).map { it.displayName }

        if (popupWindow == null) {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.history_filter_pop_up, null)

            // Get views from XML
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewFilters)
            val btnApply: Button = view.findViewById(R.id.apply_filter_button)

            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = HistoryFilterAdapter(filterOptions)
            recyclerView.adapter = adapter

            popupWindow = PopupWindow(
                view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true
            ).apply {
                elevation = 10f
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            btnApply.setOnClickListener {
                val (selectedFilter, isAscending) = adapter.getSelectedFilter()
                val sortType = SortType.fromDisplayName(selectedFilter)

                if (sortType != null) {
                    filteredHistoryItemsList = SorterUtil.filterByType(filteredHistoryItemsList, sortType, isAscending)
                    setupRecyclerView(filteredHistoryItemsList)
                    popupWindow?.dismiss()
                } else {
                    Log.e("showPopup", "Invalid sort type: $selectedFilter")
                }
            }
        }

        popupWindow?.showAsDropDown(binding.filterButton, 0, 10)
    }


    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    private fun showRemoveItemDialog(
        position: Int
    ) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.item_delete_dialog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.setCanceledOnTouchOutside(false)
        dialogView.findViewById<View>(R.id.dialog_close).setOnClickListener {
            historyAdapter.updateList(filteredHistoryItemsList)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.yes_button).setOnClickListener {
            historyAdapter.updateList(filteredHistoryItemsList)
            removeItem(position);
            dialog.dismiss();
        }

        dialogView.findViewById<View>(R.id.no_button).setOnClickListener {
            historyAdapter.updateList(filteredHistoryItemsList)
            dialog.dismiss();
        }

        dialog.show()
    }
}
