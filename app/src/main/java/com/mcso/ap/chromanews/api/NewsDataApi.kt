package com.mcso.ap.chromanews.api

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsDataApi {
    // A newsapi.org has hard limit on API request per day. If API limit is exceeded,
    // use alternative API key provided below:
    // fcedd3d6f6044ab3ba242bb98e9babbf  | cf98e9a0b37945e690f495886bc4b545
    @GET("v2/top-headlines?language=en&apiKey=fcedd3d6f6044ab3ba242bb98e9babbf&pageSize=100")
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