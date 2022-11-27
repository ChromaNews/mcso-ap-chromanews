package com.mcso.ap.chromanews.api

import android.util.Log

class NewsDataRepo(private val api: NewsDataApi) {

    private fun unpackPosts(response: NewsDataApi.NewsDataResponse): List<NewsPost> {

        val mutableList : MutableList<NewsPost> = arrayListOf()

        Log.d("ANBU: result", response.totalResults.toString())
        // Log.d("ANBU: result", response.results.toString())

        // for (element in response.results) {
        for (element in response.articles) {
                Log.d("ANBU: FOR result", element.toString())
                mutableList.add(element)
            }

        Log.d("ANBU: mutableList",  mutableList.toList().toString() )

        return mutableList.toList()
    }

    suspend fun getNews(subcategory: String): List<NewsPost> {
            return unpackPosts(api.getNews(subcategory))
        }

}