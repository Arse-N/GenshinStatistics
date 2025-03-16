package com.example.genshinstatistics.util

import com.example.genshinstatistics.model.BannerData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class BannerFetcher {

    private val client = OkHttpClient()
    private val imageBaseUrl = "https://paimon.moe/images/events/"


    fun fetchBanners(onBannersFetched: (List<BannerData>) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val banners = withContext(Dispatchers.IO) {
                val url = "https://paimon.moe/_app/immutable/chunks/timeline-377d9c44.js"
                val request = Request.Builder().url(url).build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val jsContent = response.body?.string() ?: return@use null
                            extractBannerData(jsContent)
                        } else {
                            null
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            if (banners != null) {
                onBannersFetched(banners)
            }
        }
    }

    private fun extractBannerData(jsContent: String): List<BannerData> {
        val regex = """\[(.*)\]""".toRegex()
        val matchResult = regex.find(jsContent)
        val jsonString = matchResult?.groups?.get(1)?.value ?: ""

        val jsonArray = JSONArray("[$jsonString]")

        val banners = mutableListOf<BannerData>()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Date()

        for (i in 0 until jsonArray.length()) {
            val innerArray = jsonArray.getJSONArray(i)

            for (j in 0 until innerArray.length()) {
                val jsonObject = innerArray.getJSONObject(j)
                val id = BaseUtil.generateCode()
                val name = jsonObject.optString("name")
                val startDate = jsonObject.optString("start")
                val endDate = jsonObject.optString("end")
                val imageUrl = jsonObject.optString("image")

                val start = dateFormat.parse(startDate)
                val end = dateFormat.parse(endDate)

                if (start != null && end != null && today.after(start) && today.before(end)) {
                    if (name.contains("Banner", ignoreCase = true)) {
                        banners.add(BannerData(id, name, startDate, endDate, imageBaseUrl + imageUrl))
                    }
                } else {
                    continue
                }
            }
//            if (banners.isNotEmpty()) {
////                break
//            }
        }

        return banners
    }

}
