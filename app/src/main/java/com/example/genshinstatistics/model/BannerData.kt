package com.example.genshinstatistics.model

data class BannerData(
    var id: String,
    var name: String = "",
    var start: String? = null,
    var end: String? = null,
    var imageUrl: String = "",
    var order: Int,
)
