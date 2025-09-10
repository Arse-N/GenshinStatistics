package com.example.genshinstatistics.model

data class BannerStatistics(
    val totalPulls: Int,
    val pityCount: Int,
    val fifty50Wins: Int,
    val fifty50Loses: Int,
    val fifty50WinsRecordStrike: Int,
    val fifty50LosesRecordStrike: Int,
    val currentStrike: Int
)
