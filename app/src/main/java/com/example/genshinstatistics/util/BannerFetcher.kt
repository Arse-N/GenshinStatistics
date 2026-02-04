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
                val name = jsonObject.optString("name", null)

                val startString = jsonObject.optString("start", null)
                val endString = jsonObject.optString("end", null)

                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                val startDate: Date? = runCatching {
                    startString?.let { sdf.parse(it) }
                }.getOrNull()

                val endDate: Date? = runCatching {
                    endString?.let { sdf.parse(it) }
                }.getOrNull()

                val imageUrl = jsonObject.optString("image", null)

                if (
                    startDate != null &&
                    endDate != null &&
                    today.after(startDate) &&
                    today.before(endDate)
                ) {
                    if (!name.isNullOrBlank() && name.contains("Banner", ignoreCase = true)) {
                        banners.add(
                            BannerData(
                                id,
                                name,
                                startString,
                                endString,
                                imageBaseUrl + imageUrl,
                                j + 1
                            )
                        )
                    }
                }

            }
        }

        return banners
    }

}
