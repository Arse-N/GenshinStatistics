package com.example.genshinstatistics.ui.history

import ItemSwiper
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.genshinstatistics.R
import com.example.genshinstatistics.adapters.HistoryItemAdapter
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.databinding.FragmentHistoryBinding
import com.example.genshinstatistics.enum.WinRateType
import com.example.genshinstatistics.enum.WishType
import com.example.genshinstatistics.model.HistoryItem
import com.example.genshinstatistics.util.BaseUtil
import com.example.genshinstatistics.util.JsonUtil
import java.util.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: HistoryItemAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val historyItemsList: ArrayList<HistoryItem> = JsonUtil.readFromJson(requireContext()) ?: ArrayList()

        setupWishTypeSpinner(binding.wishTypeSelector);
        val itemSwiper = object : ItemSwiper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        showRemoveItemDialog(
                            historyItemsList,
                            viewHolder.adapterPosition
                        )
                    }

                    ItemTouchHelper.RIGHT -> {
                        val historyItem: HistoryItem = historyItemsList.get(viewHolder.adapterPosition)
                        showItemDialog(
                            historyItemsList,
                            historyItem,
                            viewHolder.adapterPosition
                        )
                    }

                }

            }
        }

        binding.addButton.setOnClickListener {
            showItemDialog(
                historyItemsList,
                HistoryItem(
                    id = BaseUtil.generateCode(),
                ),
                null
            )
        }
        setupRecyclerView(historyItemsList)

        val itemTouchHelper = ItemTouchHelper(itemSwiper)
        itemTouchHelper.attachToRecyclerView(binding.historyItems)

        return binding.root
    }

    private fun setupRecyclerView(historyItemsList: ArrayList<HistoryItem>) {
        historyAdapter = HistoryItemAdapter(historyItemsList)

        binding.historyItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupWishTypeSpinner(wishTypeSpinner: Spinner) {
        val wishTypes = WishType.entries
        val wishNames = wishTypes.map { it.displayName }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, wishNames)
        wishTypeSpinner.adapter = adapter

        val arrowButton = view?.findViewById<ImageButton>(R.id.wish_type_arrow)

        val toggleDropdown = {
            val arrowDown = resources.getDrawable(R.drawable.ic_arrow_down, null)
            val arrowUp = resources.getDrawable(R.drawable.ic_arrow_up, null)

            if (arrowButton?.drawable == arrowDown) {
                wishTypeSpinner.performClick()
                arrowButton?.setImageDrawable(arrowUp)
            } else {
                wishTypeSpinner.performClick()
                arrowButton?.setImageDrawable(arrowDown)
            }
        }

        arrowButton?.setOnClickListener {
            toggleDropdown()
        }

        wishTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedWish = wishTypes[position]
                Log.d("SelectedWish", "Selected: ${selectedWish.name}")
                arrowButton?.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down, null))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }




    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showItemDialog(
        historyItemsList: ArrayList<HistoryItem>,
        historyItem: HistoryItem,
        position:Int?
    ) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_history_item_dialog, null)
        val nameSpinner: AutoCompleteTextView = dialogView.findViewById(R.id.item_selector)
        val wishTypeSpinner: Spinner = dialogView.findViewById(R.id.wish_type_selector)
        val winRateSpinner: Spinner = dialogView.findViewById(R.id.win_rate_selector)
        val errorText: TextView = dialogView.findViewById(R.id.item_selector_error)
        var chosenItem: String? = historyItem.name
        var chosenDate: String? = historyItem.winDate
        var chosenWishRate: Int? = historyItem.wishRate
        var chosenWinRate: String? = historyItem.winRate
        var chosenWishType: String? = historyItem.wishType

        setUpCharacterData(nameSpinner, chosenItem) { item -> chosenItem = item }
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
            historyAdapter.notifyDataSetChanged()
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
                if(position == null)
                    addNewItem(historyItem, historyItemsList)
                else
                    updateItem(position, historyItem, historyItemsList)
                dialog.dismiss();
            }

        }

        dialog.show()
    }

    private fun setUpCharacterData(
        spinner: AutoCompleteTextView,
        selectedItem: String?,
        onItemSelected: (String?) -> Unit
    ) {
        val sortedItems = ArchiveCharacterData.ITEMS
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
//        val arrowDown: ImageButton = view.findViewById(R.id.number_arrow_down)
//        val arrowUp: ImageButton = view.findViewById(R.id.number_arrow_up)

        wishRatePicker.minValue = 1
        wishRatePicker.maxValue = 90

        wishRatePicker.value = rateValue ?: 1
        onRateChanged(wishRatePicker.value)

        wishRatePicker.setOnValueChangedListener { _, _, newVal ->
            onRateChanged(newVal)
        }
//
//        val delayMillis: Long = 120
//        val handler = Handler(Looper.getMainLooper())
//
//        val incrementRunnable = object : Runnable {
//            override fun run() {
//                if (wishRatePicker.value < 90) {
//                    wishRatePicker.value++
//                    onRateChanged(wishRatePicker.value)
//                    handler.postDelayed(this, delayMillis)
//                }
//            }
//        }
//
//        val decrementRunnable = object : Runnable {
//            override fun run() {
//                if (wishRatePicker.value > 1) {
//                    wishRatePicker.value--
//                    onRateChanged(wishRatePicker.value)
//                    handler.postDelayed(this, delayMillis)
//                }
//            }
//        }

//        arrowUp.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    handler.post(incrementRunnable)
//                }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                    handler.removeCallbacks(incrementRunnable)
//                }
//            }
//            true
//        }
//
//        arrowDown.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    handler.post(decrementRunnable)
//                }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                    handler.removeCallbacks(decrementRunnable)
//                }
//            }
//            true
//        }
    }

    private fun setUpDatePicker(view: View, selectedDate: String?, onDateSelected: (String?) -> Unit) {
        val dateInput: EditText = view.findViewById(R.id.win_date_selector)
        val calendar = Calendar.getInstance()

        // Set initial values for year, month, and day
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Set the input field to the selected date or today's date
        if (!selectedDate.isNullOrEmpty()) {
            dateInput.setText(selectedDate)
        } else {
            val todayDate = "$currentDay/${currentMonth + 1}/$currentYear"
            dateInput.setText(todayDate)
        }

        // Create DatePickerDialog
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

        // Disable future dates
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        // Open the DatePickerDialog on input click
        dateInput.setOnClickListener {
            datePickerDialog.show()
        }
    }

    private fun addNewItem(historyItem: HistoryItem, historyItemsList: ArrayList<HistoryItem>) {
        historyItemsList.add(historyItem)
        historyAdapter.notifyItemInserted(historyItemsList.size - 1)
        JsonUtil.writeToJson(requireContext(), historyItemsList)
        if (historyItemsList.size > 1) {
            historyAdapter.notifyItemChanged(historyItemsList.size - 2)
        }
    }

    private fun updateItem(position: Int, newHistoryItem: HistoryItem, historyItemsList: ArrayList<HistoryItem>) {
        historyItemsList[position] = newHistoryItem;
        historyAdapter.notifyItemChanged(position)
        JsonUtil.writeToJson(requireContext(), historyItemsList)
    }

    private fun removeItem(position: Int, historyItemsList: ArrayList<HistoryItem>) {
        historyItemsList.removeAt(position)
        historyAdapter.notifyItemRemoved(position)
        JsonUtil.writeToJson(requireContext(), historyItemsList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    private fun showRemoveItemDialog(
        historyItemsList: ArrayList<HistoryItem>,
        position: Int
    ) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.item_delete_dialog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.setCanceledOnTouchOutside(false)
        dialogView.findViewById<View>(R.id.dialog_close).setOnClickListener {
            historyAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.yes_button).setOnClickListener {
            historyAdapter.notifyDataSetChanged()
            removeItem(position, historyItemsList);
            dialog.dismiss();
        }

        dialogView.findViewById<View>(R.id.no_button).setOnClickListener {
            historyAdapter.notifyDataSetChanged()
            dialog.dismiss();
        }

        dialog.show()
    }
}
