package com.mcso.ap.chromanews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcso.ap.chromanews.api.NewsDataApi
import com.mcso.ap.chromanews.api.NewsDataRepo
import com.mcso.ap.chromanews.model.api.NewsData
import com.mcso.ap.chromanews.model.auth.FirebaseUserLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class MainViewModel(): ViewModel() {
    // Firebase
    private val firebaseAuthLiveData = FirebaseUserLiveData()

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

    // fetch news by category from newsdata.io
    fun netNewsData(){
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO){
            newsDataList.postValue(newsDataRepo.getNewsData(category))
        }
    }
}