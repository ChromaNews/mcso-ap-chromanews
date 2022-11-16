package com.mcso.ap.chromanews

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcso.ap.chromanews.api.NewsDataApi
import com.mcso.ap.chromanews.api.NewsDataRepo
import com.mcso.ap.chromanews.db.SentimentDBHelper
import com.mcso.ap.chromanews.model.auth.FirebaseUserLiveData
import com.mcso.ap.chromanews.model.sentiment.SentimentColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

class MainViewModel(): ViewModel() {

    private val TAG = "MainViewModel"

    // Firebase
    private val firebaseAuthLiveData = FirebaseUserLiveData()

    // NewsData repo and api
    private val newsDataApi = NewsDataApi.create()
    private val newsDataRepo = NewsDataRepo(newsDataApi)

    // Sentiment data DB
    private val sentimentDataDB: SentimentDBHelper = SentimentDBHelper()
    private var sentimentColor: SentimentColor = SentimentColor(0,255, 0)

    // NewsData
    // category
    private var category = "entertainment"

    // live data
    private val newsDataList = MutableLiveData<NewsDataApi.NewsDataResponse>()
    private val ratingDateList = MutableLiveData<List<Double>>()

    // update firebase user
    fun updateUser(){
        firebaseAuthLiveData.updateUser()
    }

    // fetch news by category from newsdata.io
    fun netNewsData(){
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO){
            newsDataList.postValue(newsDataRepo.getNewsData(category))
        }
    }

    fun updateUserSentiment(){
        // TODO - random number for rating. Replace with rating from sentiment API
        val rating: Double = String.format(
            "%.6f", Random.nextDouble(0.1,0.99)
        ).toDouble()
        firebaseAuthLiveData.getUser()?.let { sentimentDataDB.createSentimentRating(it, rating) }
    }

    fun calculateRating(){
        firebaseAuthLiveData.getUser()?.let {
            sentimentDataDB.getTotalRating(it, ratingDateList)
        }!!

    }

    fun observeRatingByDate(): LiveData<List<Double>> {
        return ratingDateList
    }

    fun calculateSentimentColorCode(ratingByDate: List<Double>){
        Log.d(TAG, "total rating: ${ratingByDate.sum() / ratingByDate.size}")

        when (val sentimentValue = (ratingByDate.sum() / ratingByDate.size)){
            0.0 -> {
                Log.d(TAG, "Neutral Sentiment value = $sentimentValue")
                sentimentColor = SentimentColor(0, 255, 0)
            }
            in 0.00..1.0 -> {
                Log.d(TAG, "Positive Sentiment value = $sentimentValue")
                calculatePositiveColor(sentimentValue)
            }
            in -1.0..-0.01 -> {
                Log.d(TAG, "Negative Sentiment value = $sentimentValue")
                calculateNegativeColor(sentimentValue)
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