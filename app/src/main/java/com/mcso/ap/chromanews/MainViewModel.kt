package com.mcso.ap.chromanews

import android.util.Log
import androidx.lifecycle.*
import com.mcso.ap.chromanews.api.NewsDataApi
import com.mcso.ap.chromanews.api.NewsDataRepo
import com.mcso.ap.chromanews.api.NewsPost
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

    // sentiment color rating data
    private val ratingDateList = MutableLiveData<List<Double>>()

    // newsdata
    private val newsDataList = MediatorLiveData<List<NewsPost>>().apply {
        value = mutableListOf()
    }
    private var category = MutableLiveData<String>().apply {
        value = "entertainment"
    }

    // update firebase user
    fun updateUser(){
        firebaseAuthLiveData.updateUser()
    }

    private var title = MutableLiveData<String>()
    private var searchTerm: MutableLiveData<String> = MutableLiveData("")
    var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)
    var subreddit = MutableLiveData<String>().apply {
        value = "entertainment"
    }

    var fetchList = MediatorLiveData<List<NewsPost>>().apply {
        value = mutableListOf()
    }

    var subredditList = MediatorLiveData<List<NewsPost>>().apply {
        value = mutableListOf()
    }

    // handling favorites
    private var favPostsList = MutableLiveData<List<NewsPost>>().apply{
        value = mutableListOf()
    }

    init{
        netPosts()
    }

    fun netPosts(){
        viewModelScope.launch (
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO)
        {
            Log.d(TAG, fetchList.value.toString())
            fetchList.postValue(newsDataRepo.getNews(category.value.toString()))
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

    fun observeCategory(): LiveData<String> {
        return category
    }

    fun setCategory(newCategory: String){
        category.value = newCategory
    }

    fun observeLiveData(): LiveData<List<NewsPost>> {
        return fetchList
    }

    fun observeLiveFavoritesData(): LiveData<List<NewsPost>> {
        return favPostsList
    }

    fun removeFav(albumRec: NewsPost) {
        val localList = favPostsList.value?.toMutableList()
        localList?.let {
            it.remove(albumRec)
            favPostsList.value = it
        }
        Log.d("ANBU removeFav:", favPostsList.value?.size.toString())
        Log.d("ANBU removeFav List:", favPostsList.value.toString())
    }


    fun isFav(albumRec: NewsPost): Boolean {
        var fav =  favPostsList.value?.contains(albumRec) ?: false

        Log.d("ANBU: isFav : ", albumRec.toString())
        Log.d("ANBU: isFav exists: ", fav.toString())
        return fav
    }

    fun getItemAt(position: Int) : NewsPost? {
        val localList = fetchList.value?.toList()
        localList?.let {
            if( position >= it.size ) return null
            return it[position]
        }
        return null
    }

    fun addFav(albumRec: NewsPost) {
        // favPosts.add(albumRec)
        // favPostsList.value = favPosts
        val localList = favPostsList.value?.toMutableList()
        localList?.let {
            it.add(albumRec)
            favPostsList.value = it
        }
        Log.d("ANBU addFav ", favPostsList.value.toString())
    }

    fun getFavoriteCount() : Int {
        return favPostsList.value!!.size
    }

    // BEGIN RATING SECTION
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