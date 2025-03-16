package com.example.genshinstatistics.model

import com.example.genshinstatistics.enums.ItemType

data class GoalItem(
    var id: String,
    var name: String = "",
    var ascension: String = "",
    var type: ItemType = ItemType.CHARACTER,
    var order: Int? = null
)
