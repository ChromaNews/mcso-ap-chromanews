package com.mcso.ap.chromanews.ui.sentiment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mcso.ap.chromanews.model.MainViewModel
import com.mcso.ap.chromanews.databinding.FragmentMoodColorBinding
import com.mcso.ap.chromanews.model.sentiment.SentimentColor
import java.util.*
import kotlin.math.abs

class MoodColorFragment : Fragment() {

    companion object{
        private val TAG = "MoodColorFragment"
    }

    private lateinit var _binding: FragmentMoodColorBinding
    private val viewModel: MainViewModel by viewModels()
    private var sentimentColor: SentimentColor = SentimentColor(0,255, 0)
    private lateinit var sentimentType: String
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMoodColorBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // viewModel.calculateRating()

        viewModel.observeRatingByDate().observe(viewLifecycleOwner){
            calculateSentimentColorCode(it)
            val sentimentHexColorCode = getSentimentColorInHex()
            val hexMoodColor = Color.parseColor(sentimentHexColorCode)

            // color code
            binding.colorCode.text = sentimentHexColorCode.uppercase(Locale.getDefault())
            when (sentimentType){
                "negative" -> {
                    binding.colorCode.setTextColor(Color.parseColor("#000000"))
                }
                else -> {
                    binding.colorCode.setTextColor(Color.parseColor("#FFFFFF"))
                }
            }
            binding.colorCode.setBackgroundColor(hexMoodColor)

            // mind color
            binding.moodColor.setColorFilter(hexMoodColor)
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (view != null && menuVisible){
       //   viewModel.calculateRating()
        }
    }

    private fun getSentimentColorInHex(): String {
        val rHex = getHexCode(sentimentColor.red)
        val gHex = getHexCode(sentimentColor.green)
        val bHex = getHexCode(sentimentColor.blue)
        return "#$rHex$gHex$bHex"
    }

    private fun getHexCode(intColorCode: Int): String {
        return Integer.toHexString(intColorCode).padStart(2, '0')
    }

    private fun calculateSentimentColorCode(ratingByDate: List<Double>){
        val sentimentValue = (ratingByDate.sum() / ratingByDate.size)
        Log.d(TAG, "total rating: $sentimentValue")


        when (sentimentValue){
            in 0.01..1.0 -> {
                Log.d(TAG, "Positive Sentiment value = $sentimentValue")
                calculatePositiveColor(sentimentValue)
                sentimentType = "positive"
            }
            in -1.0..-0.01 -> {
                Log.d(TAG, "Negative Sentiment value = $sentimentValue")
                calculateNegativeColor(sentimentValue)
                sentimentType = "negative"
            }
            else -> {
                Log.d(TAG, "Neutral Sentiment value = $sentimentValue")
                sentimentColor = SentimentColor(0, 255, 0)
                sentimentType = "neutral"
            }
        }
        Log.d(TAG, "mood color: [${sentimentColor.red}, ${sentimentColor.green}, ${sentimentColor.blue}]")
    }

    private fun calculateNegativeColor(sentimentNum: Double){
        val red = (abs(sentimentNum) * 255).toInt()
        val green = abs((1 + sentimentNum) * 255).toInt()
        sentimentColor = SentimentColor(red, green, 0)
    }

    private fun calculatePositiveColor(sentimentNum: Double){
        val blue = (abs(sentimentNum) * 255).toInt()
        val green = abs((1 - sentimentNum) * 255).toInt()
        sentimentColor = SentimentColor(0, green, blue)
    }
}