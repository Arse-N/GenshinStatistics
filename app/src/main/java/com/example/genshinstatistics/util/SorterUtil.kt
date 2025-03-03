package com.example.genshinstatistics.util

import com.example.genshinstatistics.enums.SortType
import com.example.genshinstatistics.enums.WishType
import com.example.genshinstatistics.model.HistoryItem

object SorterUtil {

    fun sortAndFilter(
        list: ArrayList<HistoryItem>,
        sortBy: SortType? = null,
        ascending: Boolean = false,
        filterByWishType: String? = WishType.CHARACTER_WISH.displayName,
    ): ArrayList<HistoryItem> {

        val filteredList = if (filterByWishType != null) {
            ArrayList(list.filter { it.wishType == filterByWishType })
        } else {
            ArrayList(list)
        }

        val sortedList = when (sortBy) {
            SortType.WISH_TYPE -> filteredList.sortedWith(compareBy({ BaseUtil.parseDate(it.winDate) }, { it.wishType }))
            SortType.WIN_DATE -> filteredList.sortedBy { BaseUtil.parseDate(it.winDate) }
            SortType.NAME -> filteredList.sortedBy { it.name }
            SortType.WISH_RATE -> filteredList.sortedBy { it.wishRate }
            null -> filteredList
        }
        return if (ascending) ArrayList(sortedList) else ArrayList(sortedList.reversed())
    }

    fun filterByType(
        filteredList: ArrayList<HistoryItem>,
        sortBy: SortType? = null,
        ascending: Boolean = false
    ): ArrayList<HistoryItem> {

        val sortedList = when (sortBy) {
            SortType.WISH_TYPE -> filteredList.sortedWith(compareBy({ BaseUtil.parseDate(it.winDate) }, { it.wishType }))
            SortType.WIN_DATE -> filteredList.sortedBy { BaseUtil.parseDate(it.winDate) }
            SortType.NAME -> filteredList.sortedBy { it.name }
            SortType.WISH_RATE -> filteredList.sortedBy { it.wishRate }
            null -> filteredList
        }
        return if (ascending) ArrayList(sortedList) else ArrayList(sortedList.reversed())
    }


}
