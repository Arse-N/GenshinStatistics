package com.example.genshinstatistics.model

import com.example.genshinstatistics.enum.ItemType

data class Character(
    val id: Int? = null,
    val icon: Int? = null,
    val iconBgColor: Int? = null,
    val name: String? = null,
    val element: Int? = null,
    val region: Region? = null,
    val birthdate: String? = null,
    val rank: Int? = null,
    val type: ItemType? = null,
    val rarity: Int? = null,
)
