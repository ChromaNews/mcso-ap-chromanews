package com.mcso.ap.chromanews.model

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.core.text.clearSpans
import androidx.lifecycle.*
import com.mcso.ap.chromanews.api.*
import com.mcso.ap.chromanews.db.SentimentDBHelper
import com.mcso.ap.chromanews.model.api.SentimentData
import com.mcso.ap.chromanews.model.auth.FirebaseUserLiveData
import com.mcso.ap.chromanews.model.conflict.Conflicts
import com.mcso.ap.chromanews.model.conflict.ConflictsResponse
import com.mcso.ap.chromanews.model.sentiment.SentimentColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mcso.ap.chromanews.db.NewsDBHelper
import com.mcso.ap.chromanews.model.savedNews.NewsMetaData
import com.mcso.ap.chromanews.ui.ReadNews

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

    // conflicts api and data
    private val api: ConflictsApi = ConflictsApi.create()
    private val repo = ConflictRepo(api)
    private var conflictLiveData = MutableLiveData<ConflictsResponse>()
    private var showProgress = MutableLiveData<Boolean>()

    // newsdata

    // fun getCurrentUser() : FirebaseUser? {
    //    return firebaseAuth.currentUser
    //}

    // for bookmarked news
    private val savednewsDataDB: NewsDBHelper = NewsDBHelper()
    private var savedNewsList = MutableLiveData<List<NewsMetaData>>()

    fun fetchSavedNewsList() {
        savednewsDataDB.fetchSavedNews(savedNewsList)
    }

    fun observeSavedNewsList(): MutableLiveData<List<NewsMetaData>> {
        return savedNewsList
    }

    fun getSavedNewsCount(): Int? {
        return savedNewsList.value?.size
    }

    private var category = MutableLiveData<String>().apply{
        value = "business"
    }

    fun getNewsMeta(position: Int) : NewsMetaData {
        val news = savedNewsList.value?.get(position)
        return news!!
    }

    fun createNewsMetadata(uuid: String, title: String, pubDate: String, description: String,
                           imageURL: String, link: String){

        val newsMeta = NewsMetaData(
            newsID = uuid,
            title = title,
            description = description,
            imageURL = imageURL,
            link = link,
            pubDate = pubDate,
            firestoreID = ""
        )
        savednewsDataDB.createNewsMetadata(newsMeta, savedNewsList)
    }

    fun getSavedNewsList(): MutableLiveData<List<NewsMetaData>> {
        return savedNewsList
    }

    fun removeSavedNews(position: Int){
        val news = getNewsMeta(position)
        var newsPost: NewsPost? = null

        for (item in favPostsList.value!!){
            if (item.title == news.title){
                newsPost = item
                break
            }
        }
        savednewsDataDB.removeNewsMetadata(news, savedNewsList)
        if (newsPost != null) {
            removeFav(newsPost)
        }
    }

    // update firebase user
    fun updateUser(){
        firebaseAuthLiveData.updateUser()
    }

    private var searchTerm: MutableLiveData<String> = MutableLiveData("")
    var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)

    var fetchList = MediatorLiveData<List<NewsPost>>().apply {
        value = mutableListOf()
    }

    // handling favorites
    private var favPostsList = MutableLiveData<List<NewsPost>>().apply{
        value = mutableListOf()
    }

    // setting search term
    fun setSearchTerm(s: String) {
        searchTerm.value = s
    }

    init{
        fetchSavedNewsList()
    }

    fun netPosts(){
        viewModelScope.launch (
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO)
        {
            Log.d("ANBU:", "Network Fetch")
            fetchList.postValue(category.value?.let { newsDataRepo.getNews(category.value.toString()) })
            fetchDone.postValue(true)
        }
    }

    fun observeCategory(): MutableLiveData<String> {
        return category
    }

    fun setCategory(newCategory: String){
        Log.d("ANBU:", "Network Fetch - In Set Category")
        category.value = newCategory
    }

    fun observeLiveData(): LiveData<List<NewsPost>> {
        return fetchList
    }

    fun getCategories(): MutableLiveData<String> {
        return category
    }

    fun removeFav(albumRec: NewsPost) {
        val localList = favPostsList.value?.toMutableList()
        localList?.let {
            it.remove(albumRec)
            favPostsList.value = it
        }
    }


    fun isFav(albumRec: NewsPost): Boolean {
        var fav =  favPostsList.value?.contains(albumRec) ?: false
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
        val localList = favPostsList.value?.toMutableList()
        localList?.let {
            it.add(albumRec)
            favPostsList.value = it
        }
    }

    // Convenient place to put it as it is shared
    companion object {
        fun doOnePost(context: Context, newsPost: NewsPost) {
            // val onePostIntent = Intent(context, OneNewsPost::class.java)
            val onePostIntent = Intent(context, ReadNews::class.java)

            onePostIntent.putExtra("titleKey", newsPost.title.toString())
            onePostIntent.putExtra("descKey", newsPost.description.toString())
            onePostIntent.putExtra("imageKey", newsPost.imageURL)
            onePostIntent.putExtra("linkKey", newsPost.link)
            onePostIntent.putExtra("dateKey", newsPost.pubDate)
            onePostIntent.putExtra("authorKey", newsPost.author.toString())
            context.startActivity(onePostIntent)
        }

        fun openSavedNewsPost(context: Context, newsPost: NewsMetaData) {
            val onePostIntent = Intent(context, ReadNews::class.java)

            onePostIntent.putExtra("titleKey", newsPost.title.toString())
            onePostIntent.putExtra("descKey", newsPost.description.toString())
            onePostIntent.putExtra("imageKey", newsPost.imageURL)
            onePostIntent.putExtra("linkKey", newsPost.link)
            onePostIntent.putExtra("dateKey", newsPost.pubDate)
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

    // conflicts
    fun netConflict(country: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO){
            showProgress.postValue(true)
            conflictLiveData.postValue(repo.getConflictData(country))
            showProgress.postValue(false)
        }
    }

    fun observeConflictData(): LiveData<ConflictsResponse> {
        return conflictLiveData
    }

    fun getConflictForLocation(markerLocation: String): Conflicts? {
        var conflictInfo: Conflicts? = null
        val conflicts: List<Conflicts>? = conflictLiveData.value?.conflictList?.filter {
            conflicts -> conflicts.location == markerLocation
        }

        if (conflicts != null && conflicts.isNotEmpty()) {
            conflictInfo = conflicts[0]
        }

        return conflictInfo
    }

    fun observeShowProgress(): LiveData<Boolean> {
        return showProgress
    }
}