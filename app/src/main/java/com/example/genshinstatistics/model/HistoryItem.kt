package com.example.genshinstatistics.model

data class HistoryItem(
    var id: String? = null,
    var name: String? = null,
    var wishRate: Int? = null,
    var winRate: Int? = null,
    var wishRateColor: Int? = null,
    var winDate: String? = null
)
