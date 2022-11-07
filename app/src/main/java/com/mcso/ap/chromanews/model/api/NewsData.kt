package com.mcso.ap.chromanews.model.api

import com.google.gson.annotations.SerializedName

data class NewsData (
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("content")
    val content: String,
)