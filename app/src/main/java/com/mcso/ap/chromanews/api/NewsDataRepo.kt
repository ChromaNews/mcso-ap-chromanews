package com.mcso.ap.chromanews.api

import android.text.SpannableString
import android.util.Log
import com.google.gson.GsonBuilder

class NewsDataRepo(private val api: NewsDataApi) {

    val gson = GsonBuilder().registerTypeAdapter(
        SpannableString::class.java, NewsDataApi.SpannableDeserializer()
    ).create()

   // suspend fun getNews(category: String): NewsDataApi.NewsDataResponse {
   //     return api.getNews(category)
   // }

   // suspend fun getCategories(): NewsDataApi.NewsDataResponse {
   //     return api.getCategories()
   // }


    private fun unpackPosts(response: NewsDataApi.NewsDataResponse): List<NewsPost> {
        // XXX Write me.

        val mutableList : MutableList<NewsPost> = arrayListOf()

        Log.d("ANBU: result", response.totalResults.toString())
        Log.d("ANBU: result", response.results.toString())

        for (element in response.results) {
                Log.d("ANBU: FOR result", element.toString())
                mutableList.add(element)
        }

        //Log.d("ANBU: data are result", response.results.toString())
        Log.d("ANBU: mutableList",  mutableList.toList().toString() )

        return mutableList.toList()
    }

    suspend fun getNews(subcategory: String): List<NewsPost> {
            //Log.d("ANBU: getPosts-2", api.getNews(subcategory).data.results.size.toString())

            return unpackPosts(api.getNews(subcategory))
        }

}