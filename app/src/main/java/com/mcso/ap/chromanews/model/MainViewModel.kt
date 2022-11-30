package com.mcso.ap.chromanews.model

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.lifecycle.*
import com.mcso.ap.chromanews.api.*
import com.mcso.ap.chromanews.db.SentimentDBHelper
import com.mcso.ap.chromanews.model.api.SentimentData
import com.mcso.ap.chromanews.model.auth.FirebaseUserLiveData
import com.mcso.ap.chromanews.model.conflict.Conflicts
import com.mcso.ap.chromanews.model.conflict.ConflictsResponse
import androidx.core.text.clearSpans
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mcso.ap.chromanews.db.NewsDBHelper
import com.mcso.ap.chromanews.model.savedNews.NewsMetaData
import com.mcso.ap.chromanews.ui.ReadNews

class MainViewModel(): ViewModel() {

    private val TAG = "MainViewModel"

    private var searchTerm: MutableLiveData<String> = MutableLiveData("")
    private var title = MutableLiveData<String>()

    // Firebase
    private val firebaseAuthLiveData = FirebaseUserLiveData()

    // NewsData repo and api
    private var category = "business"
    private val newsDataApi = NewsDataApi.create()
    private val newsDataRepo = NewsDataRepo(newsDataApi)

    // Sentiment Analyzer api / response
    private val sentimentAnalyzerApi = SentimentAnalyzerApi.create()
    private val sentimentAnalyzerRepo = SentimentAnalyzerRepo(sentimentAnalyzerApi)
    private val sentimentResponse = MutableLiveData<SentimentData>()

    // Sentiment data DB
    private val sentimentDataDB: SentimentDBHelper = SentimentDBHelper()

    // sentiment color rating data
    private val ratingDateList = MutableLiveData<List<Double>>()

    // conflicts api and data
    private val api: ConflictsApi = ConflictsApi.create()
    private val repo = ConflictRepo(api)
    private var conflictLiveData = MutableLiveData<ConflictsResponse>()
    private var showProgress = MutableLiveData<Boolean>()

    // for bookmarked news
    private val savednewsDataDB: NewsDBHelper = NewsDBHelper()
    private var savedNewsList = MediatorLiveData<List<NewsMetaData>>().apply {
        value = mutableListOf()
    }

    fun getFeedForCategory(){
        viewModelScope.launch (
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO)
        {
            Log.d(TAG, "fetching news feed for [${category}]")
            fetchList.postValue(newsDataRepo.getNews(category))
            fetchDone.postValue(true)
        }
    }

    fun setCategory(tabCategory: String){
        category = tabCategory
    }

    fun fetchSavedNewsList() {
        getCurrentUser()?.let { savednewsDataDB.fetchSavedNews(it, savedNewsList) }
    }

    fun observeSavedNewsList(): MutableLiveData<List<NewsMetaData>> {
        return savedNewsList
    }

    fun getSavedNewsCount(): Int? {
        if (savedNewsList.value?.isNotEmpty() == true){
            return savedNewsList.value!!.size
        }
        return 0
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
        getCurrentUser()?.let { savednewsDataDB.createNewsMetadata(it, newsMeta, savedNewsList) }
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
        getCurrentUser()?.let { savednewsDataDB.removeNewsMetadata(it, news, savedNewsList) }
        if (newsPost != null) {
            removeFav(newsPost)
        }
    }

    // update firebase user
    fun updateUser(){
        firebaseAuthLiveData.updateUser()
    }

    fun getCurrentUser(): String?{
        return firebaseAuthLiveData.getUser()
    }

    fun logoutUser(){
        firebaseAuthLiveData.logout()
    }

    var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)

    var fetchList = MediatorLiveData<List<NewsPost>>().apply {
        value = mutableListOf()
    }

    // handling favorites
    private var favPostsList = MutableLiveData<List<NewsPost>>().apply{
        value = mutableListOf()
    }

    fun bookmarkListEmpty(){
        favPostsList.value = mutableListOf()
    }

    init{
        fetchSavedNewsList()
    }

    fun observeLiveData(): LiveData<List<NewsPost>> {
        return fetchList
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
        Log.d("addFav ", favPostsList.value.toString())
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

    // Search functionalities
    fun setSearchTerm(s: String) {
        searchTerm.value = s
    }

    private fun setSpan(fulltext: SpannableString, subtext: String): Boolean {
        if( subtext.isEmpty() ) return true
        val i = fulltext.indexOf(subtext, ignoreCase = true)
        if( i == -1 ) return false
        fulltext.setSpan(
            ForegroundColorSpan(Color.BLUE), i, i + subtext.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return true
    }

    private fun removeAllCurrentSpans(){
        fetchList.value?.forEach {
            SpannableString(it.title).clearSpans()
        }
    }

    private fun filterList(): List<NewsPost> {
        Log.d(javaClass.simpleName,
            "Filter $searchTerm Q(${title.value})")

        removeAllCurrentSpans()

        val searchTermValue = searchTerm.value!!

        Log.d("Filter Searchterm", searchTermValue)
        return fetchList.value!!.filter {
            var titleFound = false
            titleFound = setSpan(SpannableString(it.title), searchTermValue)

            titleFound
        }
    }

    private var searchPosts = MediatorLiveData<List<NewsPost>>().apply {
        addSource(fetchList)  { value = filterList() }
        addSource(searchTerm)  { value = filterList() }

        value = fetchList.value
    }

    fun observeSearchPostLiveData(): LiveData<List<NewsPost>>{
        return searchPosts
    }
}