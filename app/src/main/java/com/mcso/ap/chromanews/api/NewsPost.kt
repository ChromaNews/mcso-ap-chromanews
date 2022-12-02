package com.mcso.ap.chromanews.api

import com.google.gson.annotations.SerializedName

data class NewsPost (
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val link: String,
    @SerializedName("author")
    val author: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("publishedAt")
    val pubDate : String,
    @SerializedName("urlToImage")
    val imageURL : String?,
) {
    override fun equals(other: Any?) : Boolean =
        if (other is NewsPost) {
            title == other.title
        } else {
            false
        }
}
