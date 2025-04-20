package com.example.genshinstatistics.services

import android.content.Context
import com.example.genshinstatistics.enums.WinRateType
import com.example.genshinstatistics.model.HistoryItem
import com.github.mikephil.charting.data.PieEntry

class StatisticsService(
    private var historyItems: ArrayList<HistoryItem>,
    var context: Context,
) {

fun getPieChartData(): List<PieEntry>{
    val wins: Float = historyItems.count { it.winRate == WinRateType.FIFTY_FIFTY_WIN.displayName }.toFloat()
    val loses: Float = historyItems.count { it.winRate == WinRateType.FIFTY_FIFTY_LOSE.displayName }.toFloat()
    return listOf(
        PieEntry(wins, "wins"),
        PieEntry(loses, "loses"),
    )
}

}
