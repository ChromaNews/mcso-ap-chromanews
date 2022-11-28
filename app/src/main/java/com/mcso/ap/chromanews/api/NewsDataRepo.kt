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
            return unpackPosts(api.getNews(subcategory))
        }

}