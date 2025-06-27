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
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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

    fun getBannerStatistics(bannerType: String, historyItems: ArrayList<HistoryItem>): HashMap<String, Int> {
        val filteredHistoryItems: List<HistoryItem> =
            historyItems.filter { h -> h.wishType.equals(bannerType) }
        val totalPulls = filteredHistoryItems.sumOf { it.wishRate ?: 0 }
        val pityCount = 0
        val fifty50Wins = filteredHistoryItems.count { it.winRate.equals(WinRateType.FIFTY_FIFTY_WIN.displayName) }
        val fifty50Loses = filteredHistoryItems.count { it.winRate.equals(WinRateType.FIFTY_FIFTY_LOSE.displayName) }
        val fifty50WinsRecordStrike = getRecordStrike(WinRateType.FIFTY_FIFTY_WIN, filteredHistoryItems)
        val fifty50LosesRecordStrike = getRecordStrike(WinRateType.FIFTY_FIFTY_LOSE, filteredHistoryItems)
        val currentStrike = getCurrentStrike(filteredHistoryItems)
        val statisticsData: HashMap<String, Int> = mapOf(
            "totalPulls" to totalPulls,
            "pityCount" to pityCount,
            "fifty50Wins" to fifty50Wins,
            "fifty50Loses" to fifty50Loses,
            "fifty50WinsRecordStrike" to fifty50WinsRecordStrike,
            "fifty50LosesRecordStrike" to fifty50LosesRecordStrike,
            "fifty50LosesRecordStrike" to fifty50LosesRecordStrike,
            "currentStrike" to currentStrike,
        ) as HashMap<String, Int>
        return statisticsData
    }

    private fun getRecordStrike(target: WinRateType, historyItems: List<HistoryItem>): Int {
        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

        val sorted = historyItems.filter {
            it.winRate.equals(WinRateType.FIFTY_FIFTY_WIN.displayName) or it.winRate.equals(WinRateType.FIFTY_FIFTY_LOSE.displayName)
        }.sortedBy { it.winDate?.let { it1 -> dateFormat.parse(it1) } }

        var maxStreak = 0
        var currentStreak = 0

        for (item in sorted) {
            if (item.winRate.equals(target.displayName)) {
                currentStreak++
                maxStreak = maxOf(maxStreak, currentStreak)
            } else {
                currentStreak = 0
            }
        }

        return maxStreak
    }

    private fun getCurrentStrike(historyItems: List<HistoryItem>): Int {
        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

        val sorted = historyItems.filter {
            it.winRate.equals(WinRateType.FIFTY_FIFTY_WIN.displayName) or it.winRate.equals(WinRateType.FIFTY_FIFTY_LOSE.displayName)
        }.sortedBy { it.winDate?.let { it1 -> dateFormat.parse(it1) } }.reversed()

        var currentStreak = 0
        if(sorted.isEmpty()){
            return currentStreak
        }
        val target:String? = sorted[0].winRate
        for (item in sorted) {
            if (item.winRate.equals(target)) {
                currentStreak++
            } else {
                return currentStreak;
            }
        }

        return currentStreak;
    }

    fun getLastPullWinRate(bannerType: String, historyItems: List<HistoryItem>): String? {
        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

        val sorted = historyItems.filter {
            it.wishType.equals(bannerType) and (it.winRate.equals(WinRateType.FIFTY_FIFTY_WIN.displayName) or it.winRate.equals(WinRateType.FIFTY_FIFTY_LOSE.displayName))
        }.sortedBy { it.winDate?.let { it1 -> dateFormat.parse(it1) } }.reversed()
        if (sorted.isEmpty()){
            return WinRateType.FIFTY_FIFTY_WIN.displayName
        }
        val target:String? = sorted[0].winRate


        return target
    }

}
