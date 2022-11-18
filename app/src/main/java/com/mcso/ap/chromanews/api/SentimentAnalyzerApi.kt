package com.mcso.ap.chromanews.api

import com.mcso.ap.chromanews.model.api.SentimentData
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SentimentAnalyzerApi {

    @Headers(
        "X-RapidAPI-Key: ad1aedcfffmsh83957fe2467f6ddp1f11b0jsn0ac44e296637",
        "X-RapidAPI-Host: twinword-sentiment-analysis.p.rapidapi.com"
    )
    @GET("analyze/")
    suspend fun analyzeText(@Query("text") text: String) : SentimentData

    companion object {
        var url = HttpUrl.Builder()
            .scheme("https")
            .host("twinword-sentiment-analysis.p.rapidapi.com")
            .build()

        fun create(): SentimentAnalyzerApi = create(url)
        private fun create(httpUrl: HttpUrl): SentimentAnalyzerApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SentimentAnalyzerApi::class.java)
        }
    }
}