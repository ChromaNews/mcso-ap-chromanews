package com.mcso.ap.chromanews.ui.sentiment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mcso.ap.chromanews.databinding.FragmentMoodColorBinding
import com.mcso.ap.chromanews.model.MainViewModel
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

        val explanation = "We watch and discuss news with friends and family. " +
                "Each information we read and think has an impact on our mindset. " +
                "Imagine if we could understand how what we watch and read can also tell us how it impacts us, " +
                "everytime!"
        binding.explanation.text = explanation

        // default to green
        Log.d(TAG, "setting default color")
        var defaultColor = Color.parseColor("#00FF00")
        binding.colorCode.setTextColor(Color.parseColor("#FFFFFF"))
        binding.colorCode.setBackgroundColor(defaultColor)
        binding.moodColor.setColorFilter(defaultColor)

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
        if (viewModel.getCurrentUser() != null){
            if (view != null && menuVisible){
                binding.welcome.text = getWelcomeText(viewModel.getCurrentUserName()!!)
                viewModel.calculateRating()
            }
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

        var helpText = "You are "
        when (sentimentValue){
            in 0.01..1.0 -> {
                Log.d(TAG, "Positive Sentiment value = $sentimentValue")

                helpText += if (sentimentValue < 0.5){
                    "on the positive side. keep it going!"
                } else {
                    "very positive! great job!"
                }
                binding.sentimentHelp.text = helpText
                calculatePositiveColor(sentimentValue)
                sentimentType = "positive"
            }
            in -1.0..-0.01 -> {
                Log.d(TAG, "Negative Sentiment value = $sentimentValue")

                helpText += if (sentimentValue < 0.5){
                    "on the negative side. *shrug*"
                } else {
                    "very negative! alert!"
                }
                calculateNegativeColor(sentimentValue)
                sentimentType = "negative"
            }
            else -> {
                Log.d(TAG, "Neutral Sentiment value = $sentimentValue")
                helpText += " in the sweet spot! Read more"
                sentimentColor = SentimentColor(0, 255, 0)
                sentimentType = "neutral"
            }
        }
        binding.sentimentHelp.text = helpText
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

    private fun getWelcomeText(userName: String): String {
        return "Hey $userName ! " +
                "Here is an insight into your news reading habit that impacts your mood"
    }
}