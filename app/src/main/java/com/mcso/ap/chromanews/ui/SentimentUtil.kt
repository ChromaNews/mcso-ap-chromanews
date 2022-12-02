package com.mcso.ap.chromanews.ui

import com.mcso.ap.chromanews.model.sentiment.SentimentColor
import kotlin.math.abs

class SentimentUtil {

    /**
     * Multiply the number by 255 to calculate Red intensity
     * Add the negative sentiment number from 1 and multiply with 255 for Green intensity
     * For example, if sentiment is -1, the above calculation would result in RGB = [255,0,0]
     * if sentiment is -0.5, RGB = [127,127,0]
     */
    fun calculateNegativeColor(sentimentNum: Double): SentimentColor {
        val red = (abs(sentimentNum) * 255).toInt()
        val green = abs((1 + sentimentNum) * 255).toInt()
        return SentimentColor(red, green, 0)
    }

    /**
     * Multiply the number by 255 to calculate Blue intensity
     * ASubtract the sentiment number from 1 and multiply with 255 for Green intensity
     * For example, if sentiment is 1, the above calculation would result in RGB = [0,0,255]
     * if sentiment is 0.5, RGB = [0,127,127]
     */
    fun calculatePositiveColor(sentimentNum: Double): SentimentColor {
        val blue = (abs(sentimentNum) * 255).toInt()
        val green = abs((1 - sentimentNum) * 255).toInt()
        return SentimentColor(0, green, blue)
    }

    /**
     * calculate RGB in hex. Ex [#00FF00] for green
     */
    fun getSentimentColorInHex(sentimentColor: SentimentColor): String {
        val rHex = getHexCode(sentimentColor.red)
        val gHex = getHexCode(sentimentColor.green)
        val bHex = getHexCode(sentimentColor.blue)
        return "#$rHex$gHex$bHex"
    }

    /**
     * Convert number to Hex
     */
    private fun getHexCode(intColorCode: Int): String {
        return Integer.toHexString(intColorCode).padStart(2, '0')
    }
}