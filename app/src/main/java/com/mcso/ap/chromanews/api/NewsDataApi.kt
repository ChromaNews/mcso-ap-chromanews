package com.mcso.ap.chromanews.api

import android.text.SpannableString
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mcso.ap.chromanews.model.api.NewsData
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type

interface NewsDataApi {
    // https://newsdata.io/api/1/news?apikey=YOUR_API_KEY&language=en&category=entertainment
    // @GET("api/1/news?language=en&apiKey=pub_13176ea88c295f0b89581072689b85a83fd3d")
   //  @GET("api/1/news?language=en&apiKey=pub_12303667ee10695fdd352d11f5c219d31f649")
    //2efd2e82e029486e8594bb91c7e01c92
    // old: 4fce6873f43b44d194825e906d825aa0
    // new: 2efd2e82e029486e8594bb91c7e01c92
    // Nov 27th: fcedd3d6f6044ab3ba242bb98e9babbf  | cf98e9a0b37945e690f495886bc4b545
    @GET("v2/top-headlines?language=en&apiKey=cf98e9a0b37945e690f495886bc4b545&pageSize=100")
    suspend fun getNews(@Query("category") category: String) : NewsDataResponse

    data class NewsDataResponse(
        val status: String?,
        val totalResults: Int?,
        val articles: List<NewsPost>,
    )

   data class NewsChildrenResponse(
        val data: List<NewsPost>
        )


    class SpannableDeserializer : JsonDeserializer<SpannableString> {
        // @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): SpannableString {
            return SpannableString(json.asString)
        }
    }

    companion object {

        //private const val hostUrl = "newsdata.io"
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