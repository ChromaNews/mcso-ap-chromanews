package com.mcso.ap.chromanews

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.lifecycle.*
import com.mcso.ap.chromanews.api.NewsDataApi
import com.mcso.ap.chromanews.api.NewsDataRepo
import com.mcso.ap.chromanews.api.NewsPost
import com.mcso.ap.chromanews.model.api.NewsData
import com.mcso.ap.chromanews.model.auth.FirebaseUserLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class MainViewModel(): ViewModel() {
    // Firebase
    private val firebaseAuthLiveData = FirebaseUserLiveData()
    // private val newsDataList = MutableLiveData<NewsDataApi.NewsDataResponse>()
    private val newsDataList = MediatorLiveData<List<NewsPost>>().apply {
        value = mutableListOf()
    }
    private var category = MutableLiveData<String>().apply {
        value = "entertainment"
    }

    /*
    // NewsData repo and api
    private val newsDataApi = NewsDataApi.create()
    private val newsDataRepo = NewsDataRepo(newsDataApi)

    // NewsData
    // category
    private var category = "entertainment"
    // live data
    private val newsDataList = MutableLiveData<NewsDataApi.NewsDataResponse>()

    // update firebase user
    fun updateUser(){
        firebaseAuthLiveData.updateUser()
    }

     */

    private val newsDataApi = NewsDataApi.create()
    private val newsDataRepo = NewsDataRepo(newsDataApi)
    private var title = MutableLiveData<String>()
    private var searchTerm: MutableLiveData<String> = MutableLiveData("")
    var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)
    var subreddit = MutableLiveData<String>().apply {
        value = "entertainment"
    }
    // var fetchList = MutableLiveData<List<RedditPost>>()
    var fetchList = MediatorLiveData<List<NewsPost>>().apply {
        value = mutableListOf()
    }
    // var searchList = MediatorLiveData<List<RedditPost>>()
    // var subredditList = MutableLiveData<List<RedditPost>>()
    var subredditList = MediatorLiveData<List<NewsPost>>().apply {
        value = mutableListOf()
    }
    //  var favsubredditList = MediatorLiveData<List<RedditPost>>()
    // private var selected = MutableLiveData(-1)

    // handling favorites
    private var favPostsList = MutableLiveData<List<NewsPost>>().apply{
        value = mutableListOf()
    }

    init{
        //repoFetch()
        netPosts()
        // netSubreddits()
    }
    // XXX Write netPosts/searchPosts

    // fetch news by category from newsdata.io
    fun netNewsData(){
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO)
        {
            Log.d("ANBU: ", "calling netNewsData")
            Log.d("ANBU: ", category.toString())
            // newsDataRepo.getNews(category)
            newsDataList.postValue(newsDataRepo.getNews(category.value.toString()))
        }
    }

    fun netPosts(){
        viewModelScope.launch (
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO)
        {
            Log.d("ANBU: ", fetchList.value.toString())
            fetchList.postValue(newsDataRepo.getNews(category.value.toString()))
            // fetchDone.postValue(true)
        }
    }

    fun netSubreddits(){
        viewModelScope.launch (
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO)
        {
           // subredditList.postValue(newsDataRepo.getCategories())
            fetchDone.postValue(true)
            Log.d("ANBU", subredditList.value.toString())
        }
    }

    // Looks pointless, but if LiveData is set up properly, it will fetch posts
    // from the network
    fun repoFetch() {
        Log.d("ANBU: before subreddit.value", subreddit.value.toString())

        val fetch = subreddit.value
        subreddit.value = fetch

        //val fetch = subreddit.value
        // subreddit.value = title.value
        // subreddit.value = fetch

        Log.d("ANBU: after subreddit.value", subreddit.value.toString())
    }

    fun setSearchTerm(s: String) {
        searchTerm.value = s
    }

    fun observeTitle(): LiveData<String> {
        return title
    }

    fun observeSubredditTitle(): LiveData<String> {
        return subreddit
    }

    fun observeCategory(): LiveData<String> {
        return category
    }

    fun updateTitle(){
        subreddit.value  = title.value
    }

    fun getItemCount(): Int {
        return fetchList.value!!.size
    }

    fun setCategory(newCategory: String){
        category.value = newCategory
    }

    fun setTitle(newTitle: String) {
        title.value = newTitle
        Log.d("ANBU: subreddit value-setTitle", title.value.toString())
    }
    // The parsimonious among you will find that you can call this in exactly two places
    fun setTitleToSubreddit() {
        Log.d("ANBU: subreddit value", subreddit.value.toString())
        title.value = "r/${subreddit.value}"
        Log.d("ANBU: title value", subreddit.value.toString())
    }

    // XXX Write me, set, observe, deal with favorites
    fun observeLiveData(): LiveData<List<NewsPost>> {
        return fetchList
    }

    fun observeLiveRedditData(): LiveData<List<NewsPost>> {
        return subredditList
    }


    fun observeLiveFavoritesData(): LiveData<List<NewsPost>> {
        return favPostsList
    }

    /**
    fun observeSearchPostLiveData(): LiveData<List<NewsPost>> {
        return searchPosts
    }

    fun observeSearchSubredditPostLiveData(): LiveData<List<NewsPost>> {
        return searchSubredditPosts
    }

    fun observeFavSearchPostLiveData(): LiveData<List<NewsPost>> {
        return favsubredditList
    }
    */

    // fun observeSelected(): LiveData<Int> {
    //    return selected
    // }

    // fun setSelected(num : Int) {
    //     selected.value = num
    // }

    /*
 fun observeSelFav() : LiveData<Boolean> {
     return selFav
 }*/


    fun removeFav(albumRec: NewsPost) {
        //  val localList = favPostsList.value?.toMutableList()
        // localList?.let {
        //     it.remove(albumRec)
        //     favPostsList.value = it
        // }
        // favPosts.remove(albumRec)
        // favPostsList.value = favPosts
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

    /*
    fun isSelFav(sel : Int) : Boolean {
        return if(sel == -1) {
            false
        } else {
            val item = fetchList.value!![sel]
            isFav(item)
        }
    }
     */

    fun getItemAt(position: Int) : NewsPost? {
        val localList = fetchList.value?.toList()
        localList?.let {
            if( position >= it.size ) return null
            return it[position]
        }
        return null
    }


    /*
       var selFav = MediatorLiveData<Boolean>().apply {
        addSource(selected) {
            this.value = isSelFav(it)
        }
        addSource(favPostsList) {
            val sel = selected.value ?: -1
            this.value = isSelFav(sel)
        }
    }
     */

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

    // NB: This only highlights the first match
    // Returns true if it finds a match
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
    /*
    private fun removeAllCurrentSpans(){
        // First, erase previous spans (present in liveQuotes.value).
        // Erasure is useful if e.g., movieChip is disabled
        fetchList.value?.forEach {
            it.title.clearSpans()
            it.selfText!!.clearSpans()
        }
    }

    private fun removeAllFavCurrentSpans(){
        // First, erase previous spans (present in liveQuotes.value).
        // Erasure is useful if e.g., movieChip is disabled
        favPostsList.value?.forEach {
            it.title.clearSpans()
            it.selfText!!.clearSpans()
        }
    }

    private fun removeAllSubredditCurrentSpans(){
        // First, erase previous spans (present in liveQuotes.value).
        // Erasure is useful if e.g., movieChip is disabled
        subredditList.value?.forEach {
            it.displayName!!.clearSpans()
            it.publicDescription!!.clearSpans()
        }
    }

    // NB: We always filter on the entire list of movie Quotes
    // not liveQuotes.value
    private fun filterList(): List<RedditPost> {
        Log.d(javaClass.simpleName,
            "Filter $searchTerm Q(${title.value})")
        // If you can't figure out why this is here, comment it out and
        // see what happens
        removeAllCurrentSpans()
        // We know value is not null

        val searchTermValue = searchTerm.value!!
        //SSS
        return fetchList.value!!.filter {
            var titleFound = false
            var selfTextFound = false
            titleFound = setSpan(it.title, searchTermValue)
            selfTextFound = setSpan(it.selfText!!, searchTermValue.toString())

            titleFound || selfTextFound
            // titleFound
        }
        //EEE // XXX Filter list (which refers to movieQuotes.quotes)
    }

    private fun filterFavList(): List<RedditPost> {
        Log.d(javaClass.simpleName,
            "Filter $searchTerm Q(${title.value})")
        // If you can't figure out why this is here, comment it out and
        // see what happens
        removeAllFavCurrentSpans()
        // We know value is not null

        val searchTermValue = searchTerm.value!!
        //SSS
        return favPostsList.value!!.filter {
            var titleFound = false
            var selfTextFound = false
            titleFound = setSpan(it.title, searchTermValue)
            selfTextFound = setSpan(it.selfText!!, searchTermValue.toString())

            titleFound || selfTextFound
            // titleFound
        }
        //EEE // XXX Filter list (which refers to movieQuotes.quotes)
    }

    private fun filterSubredditList(): List<RedditPost> {
        Log.d(javaClass.simpleName,
            "Filter $searchTerm Q(${title.value})")
        // If you can't figure out why this is here, comment it out and
        // see what happens
        removeAllSubredditCurrentSpans()
        // We know value is not null
        val searchTermValue = searchTerm.value!!
        //SSS
        return subredditList.value!!.filter {
            var displayFound = false
            var publicDescFound = false
            displayFound = setSpan(it.displayName!!, searchTermValue)
            // if (!displayFound){
            publicDescFound = setSpan(it.publicDescription!!, searchTermValue)
            //}

            displayFound || publicDescFound
            // displayFound
        }
        //EEE // XXX Filter list (which refers to movieQuotes.quotes)
    }


    private var searchPosts = MediatorLiveData<List<RedditPost>>().apply {
        // There is usually more logic/data maniuplation in a MediatorLiveData, but
        // we always go back to our entire movie list (movieList.quotes)
        // addSource(movieFilt)   { value = filterList()}
        // addSource(charActFilt) { value = filterList()}
        // addSource(quoteFilt)   { value = filterList()}
        addSource(fetchList)  { value = filterList() }
        addSource(searchTerm)  { value = filterList() }
        // Initial value
        value = fetchList.value
    }

    private var searchSubredditPosts = MediatorLiveData<List<RedditPost>>().apply {

        addSource(subredditList)   { value = filterSubredditList()}
        addSource(searchTerm)  { value = filterSubredditList()}
        // Initial value
        value = subredditList.value
    }

    private var favsubredditList = MediatorLiveData<List<RedditPost>>().apply {

        addSource(favPostsList)   { value = filterFavList()}
        addSource(searchTerm)  { value = filterFavList()}
        // Initial value
        value = favPostsList.value
    }


    // Convenient place to put it as it is shared
    companion object {
        fun doOnePost(context: Context, redditPost: RedditPost) {
            val onePostIntent = Intent(context, OnePost::class.java)

            // Log.d("ANBU title:", redditPost.title.toString())
            // Log.d("ANBU selfText:", redditPost.selfText.toString())
            // Log.d("ANBU imageURL:",redditPost.imageURL.toString())
            onePostIntent.putExtra("titleKey", redditPost.title.toString())
            onePostIntent.putExtra("selfTextKey", redditPost.selfText.toString())
            onePostIntent.putExtra("imageKey", redditPost.imageURL)
            onePostIntent.putExtra("thumbnailKey", redditPost.thumbnailURL)
            context.startActivity(onePostIntent)
        }
    }
     */

    fun getFavoriteCount() : Int {
        return favPostsList.value!!.size
    }
}