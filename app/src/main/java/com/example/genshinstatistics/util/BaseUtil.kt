package com.example.genshinstatistics.util

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.genshinstatistics.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object BaseUtil {

    fun generateCode(): String {
        val length = 8
        val code = StringBuilder(length)

        repeat(length) {
            val randomChar = ('a' + Random.nextInt(26))
            code.append(randomChar)
        }

        return code.toString()
    }

    fun chooseColor(context: Context, wishRate: Int): Int {
        val colorResId = when {
            wishRate <= 15 -> R.color.rarity_v1
            wishRate in 16..30 -> R.color.rarity_v2
            wishRate in 31..50 -> R.color.rarity_v3
            wishRate in 51..69 -> R.color.rarity_v4
            else -> R.color.rarity_v5
        }
        return context.getColor(colorResId)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDate(): String {
        // Get the current date
        val today = LocalDate.now()

        // Define the desired date format
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Format the date
        return today.format(formatter)
    }
}
