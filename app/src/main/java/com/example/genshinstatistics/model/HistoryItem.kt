package com.example.genshinstatistics.model

import com.example.genshinstatistics.enum.WinRateType
import com.example.genshinstatistics.enum.WishType

data class HistoryItem(
    var id: String? = null,
    var name: String? = null,
    var wishRate: Int? = null,
    var winRate: String? = null,
    var wishType: String? = null,
    var wishRateColor: Int? = null,
    var winDate: String? = null
)
