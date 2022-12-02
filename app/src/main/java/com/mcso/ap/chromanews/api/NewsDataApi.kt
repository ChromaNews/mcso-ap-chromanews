package com.mcso.ap.chromanews.api

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsDataApi {
    @GET("v2/top-headlines?language=en&apiKey=cf98e9a0b37945e690f495886bc4b545&pageSize=100")
    suspend fun getNews(@Query("category") category: String) : NewsDataResponse

    data class NewsDataResponse(
        val status: String?,
        val totalResults: Int?,
        val articles: List<NewsPost>,
    )

    companion object {
        private const val hostUrl = "newsapi.org"
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