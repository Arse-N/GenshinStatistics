package com.example.genshinstatistics.services

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.genshinstatistics.enums.ChartFilteringType
import com.example.genshinstatistics.enums.WinRateType
import com.example.genshinstatistics.enums.WishType
import com.example.genshinstatistics.model.HistoryItem
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatisticsService(
    private var historyItems: ArrayList<HistoryItem>,
    var context: Context,
) {

fun getPieChartData(): List<PieEntry>{
    val wins: Float = historyItems.count { it.winRate == WinRateType.FIFTY_FIFTY_WIN.displayName }.toFloat()
    val loses: Float = historyItems.count { it.winRate == WinRateType.FIFTY_FIFTY_LOSE.displayName }.toFloat()
    return listOf(
        PieEntry(loses, "loses"),
        PieEntry(wins, "wins"),
    )
}

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBigChartData(filter: ChartFilteringType): Pair<List<BarEntry>, List<String>> {
        val chartEntries = ArrayList<BarEntry>()
        val xLabels = ArrayList<String>()
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        var end = LocalDate.now()
        var start = end.with(DayOfWeek.MONDAY)
        var sortedHistoryItem = when (filter) {
            ChartFilteringType.WEEKLY -> {
                start = end.with(DayOfWeek.MONDAY)
                end = LocalDate.now()
                historyItems.sortedBy {
                    LocalDate.parse(it.winDate, formatter) <= end && LocalDate.parse(
                        it.winDate,
                        formatter
                    ) >= start
                }
            }
            ChartFilteringType.MONTHLY -> historyItems.sortedBy { it.winDate }
            ChartFilteringType.YEARLY -> historyItems.sortedBy { it.winDate }
            else -> historyItems.sortedBy { it.winDate }
        }

        sortedHistoryItem = sortedHistoryItem.sortedBy { it.winDate }
        sortedHistoryItem = sortedHistoryItem.filter { it.wishType == WishType.CHARACTER_WISH.displayName }
        sortedHistoryItem.forEachIndexed { index, historyItem ->
            historyItem.wishRate?.toFloat()?.let { BarEntry(index.toFloat(), it) }?.let { chartEntries.add(it) }
            historyItem.winDate?.let { xLabels.add(it) }
        }

        return Pair(chartEntries, xLabels)
    }


}
