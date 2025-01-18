package com.example.genshinstatistics.ui.history

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.genshinstatistics.R
import com.example.genshinstatistics.adapters.HistoryItemAdapter
import com.example.genshinstatistics.constants.ArchiveCharacterData
import com.example.genshinstatistics.databinding.FragmentHistoryBinding
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
        val historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val historyItemsList: ArrayList<HistoryItem> = JsonUtil.readFromJson(requireContext()) ?: ArrayList()

        binding.addButton.setOnClickListener {
            showDialog(historyItemsList)
        }
        setupRecyclerView(historyItemsList)

        return binding.root
    }

    private fun setupRecyclerView(historyItemsList: ArrayList<HistoryItem>) {
        historyAdapter = HistoryItemAdapter(historyItemsList)

        binding.historyItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialog(historyItemsList: ArrayList<HistoryItem>) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_history_item_dialog, null)
        val spinner: AutoCompleteTextView = dialogView.findViewById(R.id.item_selector)
        val numberPicker: NumberPicker = dialogView.findViewById(R.id.wish_rate_selector)

        var selectedItem: String? = null
        var selectedDate: String? = null
        var pullRate: Int = 1

        setUpSpinnerData(spinner) { item -> selectedItem = item }
        setUpWishRateData(dialogView) { rate -> pullRate = rate }
        setUpDatePicker(dialogView) { date -> selectedDate = date }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val closeButton: View = dialogView.findViewById(R.id.dialog_close)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        val doneButton: View = dialogView.findViewById(R.id.dialog_done)
        doneButton.setOnClickListener {
            var isValid = true
            val errorText: TextView = dialogView.findViewById(R.id.item_selector_error)

            // Validate selected item
            if (selectedItem.isNullOrEmpty()) {
                errorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.rarity_v5))
                isValid = false
            } else {
                errorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            // Ensure a valid date is set
            if (selectedDate.isNullOrEmpty()) {
                selectedDate = BaseUtil.getFormattedDate()
            }

            // If valid, add a new history item
            if (isValid) {
                val historyItem = HistoryItem(
                    id = BaseUtil.generateCode(),
                    name = selectedItem,
                    pullRate = pullRate,
                    pullRateColor = BaseUtil.chooseColor(pullRate),
                    winDate = selectedDate
                )
                addNewItem(historyItem, historyItemsList)
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun setUpSpinnerData(spinner: AutoCompleteTextView, onItemSelected: (String?) -> Unit) {
        val sortedItems = ArchiveCharacterData.ITEMS
            .map { it.name ?: "Unknown" }
            .sortedByDescending { it }

        val adapter = ArrayAdapter<String>(requireContext(), R.layout.custom_dropdown_item, sortedItems)

        spinner.setAdapter(adapter)
        spinner.threshold = 1
        spinner.setDropDownHeight(500)

        spinner.setOnClickListener {
            spinner.showDropDown()
        }

        spinner.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            onItemSelected(selectedItem)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpWishRateData(view: View, onRateChanged: (Int) -> Unit) {
        val numberPicker: NumberPicker = view.findViewById(R.id.wish_rate_selector)
        val arrowDown: ImageButton = view.findViewById(R.id.number_arrow_down)
        val arrowUp: ImageButton = view.findViewById(R.id.number_arrow_up)

        numberPicker.minValue = 1
        numberPicker.maxValue = 90
        numberPicker.value = 1

        val delayMillis: Long = 120
        val handler = Handler(Looper.getMainLooper())

        // Runnable for incrementing the value
        val incrementRunnable = object : Runnable {
            override fun run() {
                if (numberPicker.value < 90) {
                    numberPicker.value++
                    onRateChanged(numberPicker.value)
                    handler.postDelayed(this, delayMillis)
                }
            }
        }

        // Runnable for decrementing the value
        val decrementRunnable = object : Runnable {
            override fun run() {
                if (numberPicker.value > 1) {
                    numberPicker.value--
                    onRateChanged(numberPicker.value)
                    handler.postDelayed(this, delayMillis)
                }
            }
        }

        arrowUp.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.post(incrementRunnable)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(incrementRunnable)
                }
            }
            true
        }


        arrowDown.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.post(decrementRunnable)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(decrementRunnable)
                }
            }
            true
        }

        // Return the current value of the number picker
        onRateChanged(numberPicker.value)
    }

    private fun setUpDatePicker(view: View, onDateSelected: (String?) -> Unit) {
        val dateInput: EditText = view.findViewById(R.id.win_date_selector)

        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                view.context,
                R.style.CustomDatePicker,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    dateInput.setText(selectedDate)
                    onDateSelected(selectedDate)
                },
                year, month, day
            )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
