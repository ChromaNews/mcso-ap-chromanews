package com.mcso.ap.chromanews

import android.util.Log
import androidx.lifecycle.*
import com.mcso.ap.chromanews.api.NewsDataApi
import com.mcso.ap.chromanews.api.NewsDataRepo
import com.mcso.ap.chromanews.api.NewsPost
import com.mcso.ap.chromanews.model.auth.FirebaseUserLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(): ViewModel() {
    // Firebase
    private val firebaseAuthLiveData = FirebaseUserLiveData()

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

    private val newsDataApi = NewsDataApi.create()
    private val newsDataRepo = NewsDataRepo(newsDataApi)
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
            Log.d("ANBU: ", fetchList.value.toString())
            fetchList.postValue(newsDataRepo.getNews(category.value.toString()))
        }
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
}