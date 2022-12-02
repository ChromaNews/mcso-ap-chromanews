package com.mcso.ap.chromanews.api

import com.mcso.ap.chromanews.model.conflict.ConflictsResponse
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ConflictsApi {
    @GET("acled/read/?key=2TbbxrgT-!H6K*e!ndOV&email=thiyagarajan.angappan@utexas.edu&limit=10")
    suspend fun getConflictData(@Query("country") country: String, @Query("year") year: Int): ConflictsResponse

    companion object {
        var url = HttpUrl.Builder()
            .scheme("https")
            .host("api.acleddata.com")
            .build()

        fun create(): ConflictsApi = create(url)
        private fun create(httpUrl: HttpUrl): ConflictsApi {
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
                .create(ConflictsApi::class.java)
        }
    }
}