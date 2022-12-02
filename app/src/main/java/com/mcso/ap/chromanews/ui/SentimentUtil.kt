package com.mcso.ap.chromanews.ui

import com.mcso.ap.chromanews.model.sentiment.SentimentColor
import kotlin.math.abs

class SentimentUtil {
    fun calculateNegativeColor(sentimentNum: Double): SentimentColor {
        val red = (abs(sentimentNum) * 255).toInt()
        val green = abs((1 + sentimentNum) * 255).toInt()
        return SentimentColor(red, green, 0)
    }

    fun calculatePositiveColor(sentimentNum: Double): SentimentColor {
        val blue = (abs(sentimentNum) * 255).toInt()
        val green = abs((1 - sentimentNum) * 255).toInt()
        return SentimentColor(0, green, blue)
    }

    fun getSentimentColorInHex(sentimentColor: SentimentColor): String {
        val rHex = getHexCode(sentimentColor.red)
        val gHex = getHexCode(sentimentColor.green)
        val bHex = getHexCode(sentimentColor.blue)
        return "#$rHex$gHex$bHex"
    }

    private fun getHexCode(intColorCode: Int): String {
        return Integer.toHexString(intColorCode).padStart(2, '0')
    }
}