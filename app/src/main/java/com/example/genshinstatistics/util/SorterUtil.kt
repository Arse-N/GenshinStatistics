package com.example.genshinstatistics.util

import com.example.genshinstatistics.enum.SortType
import com.example.genshinstatistics.enum.WishType
import com.example.genshinstatistics.model.HistoryItem

object SorterUtil {

    fun sortAndFilter(
        list: ArrayList<HistoryItem>,
        sortBy: SortType? = null,
        filterByWishType: String? = WishType.CHARACTER_WISH.displayName,
        ascending: Boolean = false
    ): ArrayList<HistoryItem> {

        val filteredList = if (filterByWishType != null) {
            ArrayList(list.filter { it.wishType == filterByWishType })
        } else {
            ArrayList(list)
        }

        val sortedList = when (sortBy) {
            SortType.WISH_TYPE -> filteredList.sortedBy { it.wishType }
            SortType.WIN_DATE -> filteredList.sortedBy { BaseUtil.parseDate(it.winDate) }
            SortType.ELEMENT -> TODO()
            null -> filteredList
        }
//        val finalSortedList = sortedList.sortedBy { BaseUtil.parseDate(it.winDate) }
        return if (ascending) ArrayList(sortedList) else ArrayList(sortedList.reversed())
    }
}
