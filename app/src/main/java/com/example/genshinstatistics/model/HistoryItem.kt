package com.example.genshinstatistics.model

data class HistoryItem(
    var id: String? = null,
    var name: String = "",
    var wishRate: Int? = null,
    var winRate: String? = null,
    var wishType: String? = null,
    var wishRateColor: Int? = null,
    var winDate: String? = null
)
