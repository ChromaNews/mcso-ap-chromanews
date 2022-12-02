package com.mcso.ap.chromanews.model

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
    private val currentUserName = MutableLiveData("anonymous")

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
    private val conflictSentiment = MutableLiveData<SentimentData>()

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
            newsList.postValue(newsDataRepo.getNews(category))
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

        for (item in bookmarkPostsList.value!!){
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
    fun observeUserName(): LiveData<String>{
        return currentUserName
    }

    fun updateUser(){
        firebaseAuthLiveData.updateUser()
        currentUserName.postValue(firebaseAuthLiveData.getName())
    }

    fun getCurrentUser(): String?{
        return firebaseAuthLiveData.getUser()
    }

    fun getCurrentUserName(): String? {
        return firebaseAuthLiveData.getName()
    }

    fun logoutUser(){
        firebaseAuthLiveData.logout()
    }

    var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)

    var newsList = MediatorLiveData<List<NewsPost>>().apply {
        value = mutableListOf()
    }

    /**
     * BEGIN BOOKMARK SECTION
     */

    // handling bookmarks
    private var bookmarkPostsList = MutableLiveData<List<NewsPost>>().apply{
        value = mutableListOf()
    }

    fun bookmarkListEmpty(){
        bookmarkPostsList.value = mutableListOf()
    }

    init{
        fetchSavedNewsList()
    }

    fun observeLiveData(): LiveData<List<NewsPost>> {
        return newsList
    }

    fun removeFav(albumRec: NewsPost) {
        val localList = bookmarkPostsList.value?.toMutableList()
        localList?.let {
            it.remove(albumRec)
            bookmarkPostsList.value = it
        }
    }


    fun isFav(albumRec: NewsPost): Boolean {
        var fav =  bookmarkPostsList.value?.contains(albumRec) ?: false
        return fav
    }


    fun getItemAt(position: Int) : NewsPost? {
        val localList = newsList.value?.toList()
        localList?.let {
            if( position >= it.size ) return null
            return it[position]
        }
        return null
    }

    fun addFav(albumRec: NewsPost) {
        val localList = bookmarkPostsList.value?.toMutableList()
        localList?.let {
            it.add(albumRec)
            bookmarkPostsList.value = it
        }
    }


    /**
     * BEGIN READ NEWS SECTION
     */

    companion object {
        fun readNewsPost(context: Context, newsPost: NewsPost) {
            val readNewsIntent = Intent(context, ReadNews::class.java)

            readNewsIntent.putExtra("titleKey", newsPost.title)
            readNewsIntent.putExtra("descKey", newsPost.description.toString())
            readNewsIntent.putExtra("imageKey", newsPost.imageURL)
            readNewsIntent.putExtra("linkKey", newsPost.link)
            readNewsIntent.putExtra("dateKey", newsPost.pubDate)
            readNewsIntent.putExtra("authorKey", newsPost.author.toString())
            context.startActivity(readNewsIntent)
        }

        fun openSavedNewsPost(context: Context, newsPost: NewsMetaData) {
            val readNewsIntent = Intent(context, ReadNews::class.java)

            readNewsIntent.putExtra("titleKey", newsPost.title)
            readNewsIntent.putExtra("descKey", newsPost.description)
            readNewsIntent.putExtra("imageKey", newsPost.imageURL)
            readNewsIntent.putExtra("linkKey", newsPost.link)
            readNewsIntent.putExtra("dateKey", newsPost.pubDate)
            context.startActivity(readNewsIntent)
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

    fun netAnalyzeConflict(notes: String){
        viewModelScope.launch (
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO)
        {
            conflictSentiment.postValue(sentimentAnalyzerRepo.analyzeNewsText(notes))
        }
    }

    fun observerConflictSentiment(): LiveData<SentimentData>{
        return conflictSentiment
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

    /**
     * BEGIN SEARCH SECTION
     */

    // Set search string
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
        newsList.value?.forEach {
            SpannableString(it.title).clearSpans()
        }
    }

    private fun filterList(): List<NewsPost> {
        Log.d(javaClass.simpleName,
            "Filter $searchTerm Q(${title.value})")

        removeAllCurrentSpans()

        val searchTermValue = searchTerm.value!!

        return newsList.value!!.filter {
            var titleFound = false
            titleFound = setSpan(SpannableString(it.title), searchTermValue)

            titleFound
        }
    }

    private var searchPosts = MediatorLiveData<List<NewsPost>>().apply {
        addSource(newsList)  { value = filterList() }
        addSource(searchTerm)  { value = filterList() }

        value = newsList.value
    }

    fun observeSearchPostLiveData(): LiveData<List<NewsPost>>{
        return searchPosts
    }
}