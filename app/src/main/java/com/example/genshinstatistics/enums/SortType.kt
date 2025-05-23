package com.example.genshinstatistics.enums

enum class SortType (val displayName: String){
    WISH_TYPE ("By Wish Type"),
    WIN_DATE ("By Win Date"),
    WISH_RATE ("By Wish Rate"),
    NAME ("By Name");
    companion object {
        fun fromDisplayName(name: String): SortType? {
            return entries.firstOrNull { it.displayName == name }
        }
    }
}