package com.mcso.ap.chromanews.api

import com.mcso.ap.chromanews.model.api.NewsData
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsDataApi {
    // https://newsdata.io/api/1/news?apikey=YOUR_API_KEY&language=en&category=entertainment
    @GET("api/1/news?language=en&apiKey=pub_13176ea88c295f0b89581072689b85a83fd3d")
    suspend fun getNews(@Query("category") category: String) : NewsDataResponse

    data class NewsDataResponse(val newsResults: List<NewsData>)

    companion object {
        private const val hostUrl = "newsdata.io"
        var url = HttpUrl.Builder().scheme("https")
            .host(hostUrl)
            .build()

        fun create(): NewsDataApi = create(url)
        private fun create(url: HttpUrl) : NewsDataApi{
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            return Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsDataApi::class.java)
        }
    }
}