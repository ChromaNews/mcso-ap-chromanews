package com.mcso.ap.chromanews

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
import com.mcso.ap.chromanews.model.sentiment.SentimentColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random
import com.mcso.ap.chromanews.db.NewsDBHelper
import com.mcso.ap.chromanews.model.savedNews.NewsMetaData

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

    // fun getCurrentUser() : FirebaseUser? {
    //    return firebaseAuth.currentUser
    //}

    // for bookmarked news
    private val savednewsDataDB: NewsDBHelper = NewsDBHelper()
    private var savedNewsList = MutableLiveData<List<NewsMetaData>>()
    // private var filterbookmarkedList = MutableLiveData<List<NewsMetaData>>()

    fun fetchSavedNewsList() {
        savednewsDataDB.fetchSavedNews(savedNewsList)
        // filterbookmarkedList = savedNewsList
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

    // fun setBookmarkList(bookMlist: List<NewsMetaData>){
    //    filterbookmarkedList.postValue(bookMlist)
    // }

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

    private var title = MutableLiveData<String>()
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
        // netPosts()
        fetchSavedNewsList()
    }

    fun netPosts(){
        viewModelScope.launch (
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO)
        {
            Log.d("ANBU: Category configured", category.value.toString())
            fetchList.postValue(category.value?.let { newsDataRepo.getNews(category.value.toString()) })
            fetchDone.postValue(true)
        }
    }

    fun observeCategory(): MutableLiveData<String> {
        return category
    }

    fun setCategory(newCategory: String){
        // category.value = emptyList()
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
        Log.d("ANBU removeFav:", favPostsList.value?.size.toString())
        Log.d("ANBU removeFav List:", favPostsList.value.toString())
    }


    fun isFav(albumRec: NewsPost): Boolean {
        var fav =  favPostsList.value?.contains(albumRec) ?: false
       // var fav = savedNewsList.value.contains(albumRec) ?: false

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
        val localList = favPostsList.value?.toMutableList()
        localList?.let {
            it.add(albumRec)
            favPostsList.value = it
        }
        Log.d("ANBU addFav ", favPostsList.value.toString())
    }

    // Search posts
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

    private fun clearSpan(str: SpannableString?) {
        str?.clearSpans()
        str?.setSpan(
            ForegroundColorSpan(Color.GRAY), 0, 0,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun removeAllCurrentSpans(){
        fetchList.value?.forEach {
            SpannableString(it.title).clearSpans()
            // clearSpan(SpannableString(it.title))
           // SpannableString(it.description!!).clearSpans()
        }
    }

    private fun removeAllSubredditCurrentSpans(){
        savedNewsList.value?.forEach {
            SpannableString(it.title).clearSpans()
            // it.description!!.clearSpans()
        }
    }

    private var searchPosts = MediatorLiveData<List<NewsPost>>().apply {
        addSource(fetchList)  { value = filterList() }
        addSource(searchTerm)  { value = filterList() }

        Log.d("ANBU: NewsFeedFragment", fetchList.value.toString())
        value = fetchList.value
    }

    private var searchBookmarkPosts = MediatorLiveData<List<NewsMetaData>>().apply {
        addSource(savedNewsList) { value = filterBookmarkList()}
        addSource(searchTerm)  { value = filterBookmarkList()}

        Log.d("ANBU: BookmarkFragment", savedNewsList.value.toString())
        value = savedNewsList.value
    }

    private fun filterList(): List<NewsPost> {
        Log.d(javaClass.simpleName,
            "FeedList Filter $searchTerm Q(${title.value})")

        removeAllCurrentSpans()

        val searchTermValue = searchTerm.value!!
        return fetchList.value!!.filter {
            var titleFound = false
            // var selfTextFound = false
            titleFound = setSpan(SpannableString(it.title), searchTermValue)
            // selfTextFound = setSpan(SpannableString(it.description), searchTermValue.toString())

           // titleFound || selfTextFound
            titleFound
        }
    }

    private fun filterBookmarkList(): List<NewsMetaData> {
        Log.d(javaClass.simpleName,
            "BookmarkFragment Filter $searchTerm Q(${title.value})")

        removeAllSubredditCurrentSpans()

        val searchTermValue = searchTerm.value!!
        return savedNewsList.value!!.filter {
            var displayFound = false
            //var publicDescFound = false
            displayFound = setSpan(SpannableString(it.title), searchTermValue)
            //publicDescFound = setSpan(SpannableString(it.description), searchTermValue)

            displayFound
            //displayFound || publicDescFound
        }
    }

    fun observeSearchPostLiveData(): LiveData<List<NewsPost>>{
        return searchPosts
    }

    fun observeBookmarkSearchPostLiveData(): LiveData<List<NewsMetaData>>{
        return searchBookmarkPosts
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


    fun calculateRating(){
        firebaseAuthLiveData.getUser()?.let {
            sentimentDataDB.getTotalRating(it, ratingDateList)
        }!!
    }
}