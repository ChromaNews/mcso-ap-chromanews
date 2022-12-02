package com.mcso.ap.chromanews.api

import android.util.Log

class NewsDataRepo(private val api: NewsDataApi) {

    private fun unpackPosts(response: NewsDataApi.NewsDataResponse): List<NewsPost> {

        val mutableList : MutableList<NewsPost> = arrayListOf()

        for (element in response.articles) {
            mutableList.add(element)
        }
        return mutableList.toList()
    }

    suspend fun getNews(subcategory: String): List<NewsPost> {

        var response: NewsDataApi.NewsDataResponse = NewsDataApi.NewsDataResponse("", 0, emptyList())

        try {
            response = api.getNews(subcategory)
        } catch (e: Exception){
            Log.e(javaClass.simpleName, "Exception while fetching news for $subcategory: ${e.message}")
        }
        return unpackPosts(response)
    }

}