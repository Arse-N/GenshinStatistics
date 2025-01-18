package com.example.genshinstatistics.util

import android.os.Build
import android.view.View
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

    fun chooseColor(pullRate: Int): Int {
        return if (pullRate <= 15) {
            R.color.rarity_v1
        } else if (pullRate in 16..30) {
            R.color.rarity_v2
        } else if (pullRate in 31..50) {
            R.color.rarity_v3
        } else if (pullRate in 51..69) {
            R.color.rarity_v4
        } else {
            R.color.rarity_v5
        }
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
