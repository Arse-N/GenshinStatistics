package com.example.genshinstatistics.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

    // Method to read an image from assets/icons folder
    fun loadImageFromAssets(context: Context, imageName: String): Bitmap? {
        var inputStream: InputStream? = null
        var bitmap: Bitmap? = null
        try {
            inputStream = context.assets.open("icons/$imageName") // Path inside assets/icons/
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return bitmap
    }
}
