package com.mcso.ap.chromanews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcso.ap.chromanews.api.NewsDataApi
import com.mcso.ap.chromanews.api.NewsDataRepo
import com.mcso.ap.chromanews.db.SentimentDBHelper
import com.mcso.ap.chromanews.model.auth.FirebaseUserLiveData
import com.mcso.ap.chromanews.model.sentiment.UserSentimentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class MainViewModel(): ViewModel() {
    // Firebase
    private val firebaseAuthLiveData = FirebaseUserLiveData()

    // NewsData repo and api
    private val newsDataApi = NewsDataApi.create()
    private val newsDataRepo = NewsDataRepo(newsDataApi)

    // Sentiment data DB
    private val sentimentDataDB: SentimentDBHelper = SentimentDBHelper()

    // NewsData
    // category
    private var category = "entertainment"

    // live data
    private val newsDataList = MutableLiveData<NewsDataApi.NewsDataResponse>()

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
}