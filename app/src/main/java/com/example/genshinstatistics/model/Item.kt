package com.example.genshinstatistics.model

import com.example.genshinstatistics.enums.ItemType
import com.example.genshinstatistics.enums.Region
import com.example.genshinstatistics.enums.WeaponType

data class Item(
    val id: Int? = null,
    val icon: String? = null,
    val iconBgColor: Int? = null,
    val name: String? = null,
    val itemTypeIcon: Int? = null,
    val weaponType: WeaponType? = null,
    val region: Region? = null,
    val birthdate: String? = null,
    val rank: Int? = null,
    val type: ItemType? = null,
    val rarity: Int? = null,
)
