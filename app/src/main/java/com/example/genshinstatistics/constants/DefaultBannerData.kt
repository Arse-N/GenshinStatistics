package com.example.genshinstatistics.constants

import com.example.genshinstatistics.model.BannerData
import com.example.genshinstatistics.util.BaseUtil

object DefaultBannerData {
    val DefaultBanners: List<BannerData> by lazy {
        listOf(
            BannerData(
                id = BaseUtil.generateCode(),
                name = "Standard Banner",
                imageUrl = "https://ik.imagekit.io/h6nj8eluh/banners/standard-banner.png",
                order = 1000
            )
        )
    }
}
