package com.example.genshinstatistics.util

import com.example.genshinstatistics.model.BannerData
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class BannerFetcher {

    private val imageBaseUrl = "https://paimon.moe/images/events/"
    private val CHUNKS_PATH = "/_app/immutable/chunks/"
    private val BASE_URL = "https://paimon.moe/"
    private val client: OkHttpClient by lazy { OkHttpClient() }

    fun getBannersFetchingUrl(): String? {
        val request = Request.Builder().url(BASE_URL).build()

        return try {
            val response = client.newCall(request).execute()
            val html = response.body?.string()
            val regex = Regex("""timeline-[a-zA-Z0-9]+\.js""")
            val match = regex.find(html.orEmpty())
            match?.value?.let { "$BASE_URL$CHUNKS_PATH$it" }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun fetchBanners(scope: CoroutineScope, onBannersFetched: (List<BannerData>) -> Unit) {
        scope.launch(Dispatchers.Main) {
            val banners = withContext(Dispatchers.IO) {
                val dynamicUrl = getBannersFetchingUrl() ?: return@withContext null
//                val dynamicUrl = "https://paimon.moe/images/events/"

                val request = Request.Builder().url(dynamicUrl).build()
                try {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val jsContent = response.body?.string() ?: return@use null
                            extractBannerData(jsContent)
                        } else null
                    }
                } catch (e: Exception) {
//                    e.printStackTrace()
                    null
                }
            }

            banners?.let { onBannersFetched(it) }
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
                        banners.add(BannerData(id, name, startDate, endDate, imageBaseUrl + imageUrl, j+1))
                    }
                } else {
                    continue
                }
            }
        }

        return banners
    }

}
