package com.example.genshinstatistics.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.genshinstatistics.model.BannerData
import com.example.genshinstatistics.model.GoalItem
import com.example.genshinstatistics.model.HistoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream

object JsonUtil {
    private const val HISTORY_ITEM_FILE_NAME = "history_items.json"
    private const val BANNERS_FILE_NAME = "banners_data.json"
    private const val GOAL_ITEMS_FILE_NAME = "goal_items.json"

    fun writeToJson(context: Context, remindersList: ArrayList<HistoryItem>) {
        val gson = Gson()
        val jsonString = gson.toJson(remindersList)
        val file = File(context.getExternalFilesDir(null), HISTORY_ITEM_FILE_NAME)

        try {
            FileWriter(file).use { writer ->
                writer.write(jsonString)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Method to read JSON data from file (same as before)
    fun readFromJson(context: Context): ArrayList<HistoryItem>? {
        val file = File(context.getExternalFilesDir(null), HISTORY_ITEM_FILE_NAME)
        if (!file.exists()) {
            return null
        }

        val gson = Gson()
        return try {
            FileReader(file).use { reader ->
                val listType = object : TypeToken<ArrayList<HistoryItem>>() {}.type
                val result: ArrayList<HistoryItem>? = gson.fromJson(reader, listType)

                // Check if the result is valid (not null and not empty)
                if (result != null && result.isNotEmpty()) {
                    result // Return the valid result
                } else {
                    null // Return null if the list is empty or invalid
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun writeToBannerJson(context: Context, banners: List<BannerData>) {
        val gson = Gson()
        val jsonString = gson.toJson(banners)
        val file = File(context.getExternalFilesDir(null), BANNERS_FILE_NAME)

        try {
            FileWriter(file).use { writer ->
                writer.write(jsonString)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readFromBannersJson(context: Context): List<BannerData>? {
        val file = File(context.getExternalFilesDir(null), BANNERS_FILE_NAME)
        if (!file.exists()) {
            return null
        }

        val gson = Gson()
        return try {
            FileReader(file).use { reader ->
                val listType = object : TypeToken<ArrayList<BannerData>>() {}.type
                val result: List<BannerData>? = gson.fromJson(reader, listType)

                // Check if the result is valid (not null and not empty)
                if (result != null && result.isNotEmpty()) {
                    result // Return the valid result
                } else {
                    null // Return null if the list is empty or invalid
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun writeToGoalJson(context: Context, goalItems: ArrayList<GoalItem>) {
        val gson = Gson()
        val jsonString = gson.toJson(goalItems)
        val file = File(context.getExternalFilesDir(null), GOAL_ITEMS_FILE_NAME)

        try {
            FileWriter(file).use { writer ->
                writer.write(jsonString)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readFromGoalJson(context: Context): ArrayList<GoalItem>? {
        val file = File(context.getExternalFilesDir(null), GOAL_ITEMS_FILE_NAME)
        if (!file.exists()) {
            return null
        }

        val gson = Gson()
        return try {
            FileReader(file).use { reader ->
                val listType = object : TypeToken<ArrayList<GoalItem>>() {}.type
                val result: ArrayList<GoalItem>? = gson.fromJson(reader, listType)

                // Check if the result is valid (not null and not empty)
                if (result != null && result.isNotEmpty()) {
                    result // Return the valid result
                } else {
                    null // Return null if the list is empty or invalid
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}
