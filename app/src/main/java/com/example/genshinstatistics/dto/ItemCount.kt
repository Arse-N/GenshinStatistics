package com.example.genshinstatistics.dto

import com.example.genshinstatistics.enums.ItemType

data class ItemCount(
    val name: String?,
    val imageUrl: String?,
    val itemBg: Int?,
    val type: ItemType,
    val count: Int
)
