package com.example.genshinstatistics.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.genshinstatistics.model.HistoryItem

class StatisticViewModel : ViewModel() {

    private val _historyItems = MutableLiveData<List<HistoryItem>>().apply {
        value = listOf()  // Initialize with an empty list or mock data
    }
    val historyItems: LiveData<List<HistoryItem>> = _historyItems

    fun setHistoryItems(items: List<HistoryItem>) {
        _historyItems.value = items
    }
}
