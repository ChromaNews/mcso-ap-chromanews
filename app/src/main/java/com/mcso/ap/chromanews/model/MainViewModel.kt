package com.mcso.ap.chromanews.model

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import com.mcso.ap.chromanews.ui.OneNewsPost
import com.mcso.ap.chromanews.api.*
import com.mcso.ap.chromanews.db.SentimentDBHelper
import com.mcso.ap.chromanews.model.api.SentimentData
import com.mcso.ap.chromanews.model.auth.FirebaseUserLiveData
import com.mcso.ap.chromanews.model.sentiment.SentimentColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(): ViewModel() {

    private val TAG = "MainViewModel"

    // Firebase
    private val firebaseAuthLiveData = FirebaseUserLiveData()

    // NewsData repo and api
    private val newsDataApi = NewsDataApi.create()
    private val newsDataRepo = NewsDataRepo(newsDataApi)

    // Sentiment Analyzer api / response
    private val sentimentAnalyzerApi = SentimentAnalyzerApi.create()
    private val sentimentAnalyzerRepo = SentimentAnalyzerRepo(sentimentAnalyzerApi)
    private val sentimentResponse = MutableLiveData<SentimentData>()

    // Sentiment data DB
    private val sentimentDataDB: SentimentDBHelper = SentimentDBHelper()
    private var sentimentColor: SentimentColor = SentimentColor(0,255, 0)

    // sentiment color rating data
    private val ratingDateList = MutableLiveData<List<Double>>()

    // newsdata
    private val newsDataList = MediatorLiveData<List<NewsPost>>().apply {
        value = mutableListOf()
    }

    private var category = MutableLiveData<List<String>>().apply{
        value = mutableListOf("top")
    }

    private var default_category = MutableLiveData<List<String>>().apply{
        value = mutableListOf("top", "health", "business", "science")
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
        // netPosts()
    }

    fun netPosts(){
        viewModelScope.launch (
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO)
        {
            Log.d("ANBU: ", fetchList.value.toString())
            fetchList.postValue(category.value?.let { newsDataRepo.getNews(it.joinToString(','.toString())) })
            fetchDone.postValue(true)
        }
    }

    fun observeCategory(): MutableLiveData<List<String>> {
        return category
    }

    fun setCategory(newCategory: MutableList<String>){
        category.value = emptyList()
        category.value = newCategory
    }

    fun setDefaultCategory(){
        category.value = default_category.value
    }

    fun observeLiveData(): LiveData<List<NewsPost>> {
        return fetchList
    }

    fun getCategories(): MutableLiveData<List<String>> {
        return category
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


    // Convenient place to put it as it is shared
    companion object {
        fun doOnePost(context: Context, newsPost: NewsPost) {
            val onePostIntent = Intent(context, OneNewsPost::class.java)

            onePostIntent.putExtra("titleKey", newsPost.title.toString())
            onePostIntent.putExtra("descKey", newsPost.description.toString())
            onePostIntent.putExtra("imageKey", newsPost.imageURL)
            onePostIntent.putExtra("linkKey", newsPost.link)
            onePostIntent.putExtra("dateKey", newsPost.pubDate)
            onePostIntent.putExtra("authorKey", newsPost.creator.toString())
            context.startActivity(onePostIntent)
        }
    }

    /**
     * BEGIN RATING SECTION
     */
    fun netAnalyzeNews(newsText: String){
        viewModelScope.launch (
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO)
        {
            val response = sentimentAnalyzerRepo.analyzeNewsText(newsText)
            sentimentResponse.postValue(sentimentAnalyzerRepo.analyzeNewsText(newsText))
        }
    }

    fun observeSentimentScore(): LiveData<SentimentData>{
        return sentimentResponse
    }

    fun updateUserSentiment(score: Double){
        firebaseAuthLiveData.getUser()?.let { sentimentDataDB.createSentimentRating(it, score) }
    }

    // Color
    fun observeRatingByDate(): LiveData<List<Double>> {
        return ratingDateList
    }

    fun calculateRating(){
        firebaseAuthLiveData.getUser()?.let {
            sentimentDataDB.getTotalRating(it, ratingDateList)
        }!!
    }
}