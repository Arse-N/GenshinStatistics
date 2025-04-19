package com.example.genshinstatistics.model

import com.example.genshinstatistics.enums.GoalItemStatus
import com.example.genshinstatistics.enums.ItemType

data class GoalItem(
    var id: String,
    var name: String = "",
    var ascension: String = "C",
    var goalCount: Int = 0,
    var type: ItemType = ItemType.CHARACTER,
    var status: GoalItemStatus = GoalItemStatus.TODO,
    var obtainedDate: String = "",
    var obtained: Int = 0,
    var remaining: Int = 0,
    var order: Int? = null,
)
