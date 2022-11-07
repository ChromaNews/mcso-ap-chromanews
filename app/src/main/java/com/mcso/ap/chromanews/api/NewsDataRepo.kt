package com.mcso.ap.chromanews.api

class NewsDataRepo(private val api: NewsDataApi) {
    suspend fun getNewsData(category: String): NewsDataApi.NewsDataResponse {
        return api.getNews(category)
    }
}