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
import com.mcso.ap.chromanews.ui.SentimentUtil
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

    private val sentimentUtil = SentimentUtil()

    // color codes
    private val greenHexCode = "#00FF00"
    private val whiteHexCode = "#FFFFFF"
    private val blackHexCode = "#000000"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
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

        // default to green (neutral sentiment)
        Log.d(TAG, "setting default color")
        val defaultColor = Color.parseColor(greenHexCode)
        binding.colorCode.setTextColor(Color.parseColor(whiteHexCode))
        binding.colorCode.setBackgroundColor(defaultColor)
        binding.moodColor.setColorFilter(defaultColor)

        viewModel.observeRatingByDate().observe(viewLifecycleOwner){
            calculateSentimentColorCode(it)
            val sentimentHexColorCode = sentimentUtil.getSentimentColorInHex(sentimentColor)
            val hexMoodColor = Color.parseColor(sentimentHexColorCode)

            // color code
            binding.colorCode.text = sentimentHexColorCode.uppercase(Locale.getDefault())
            when (sentimentType){
                "negative" -> {
                    binding.colorCode.setTextColor(Color.parseColor(blackHexCode))
                }
                else -> {
                    binding.colorCode.setTextColor(Color.parseColor(whiteHexCode))
                }
            }
            binding.colorCode.setBackgroundColor(hexMoodColor)

            // mind image color
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

    private fun calculateSentimentColorCode(ratingByDate: List<Double>){
        val sentimentValue = (ratingByDate.sum() / ratingByDate.size)
        Log.d(TAG, "total rating: $sentimentValue")

        var helpText = "You are "
        when (sentimentValue){
            in 0.01..1.0 -> {
                sentimentType = "positive"
                helpText += if (sentimentValue < 0.5){
                    "on the positive side. keep it going!"
                } else {
                    "very positive! great job!"
                }
                sentimentColor = sentimentUtil.calculatePositiveColor(sentimentValue)
            }
            in -1.0..-0.01 -> {
                sentimentType = "negative"
                helpText += if (sentimentValue > -0.5){
                    "on the negative side. *shrug*"
                } else {
                    "very negative! alert!"
                }
                sentimentColor = sentimentUtil.calculateNegativeColor(sentimentValue)
            }
            else -> {
                sentimentType = "neutral"
                helpText += " in the sweet spot! Read more"
                sentimentColor = SentimentColor(0, 255, 0)
            }
        }
        Log.d(TAG, "$sentimentType value = $sentimentValue")
        binding.sentimentHelp.text = helpText
        Log.d(TAG, "mood color: [${sentimentColor.red}, ${sentimentColor.green}, ${sentimentColor.blue}]")
    }

    private fun getWelcomeText(userName: String): String {
        return "Hey $userName ! " +
                "Here is an insight into your news reading habit that impacts your mood"
    }
}