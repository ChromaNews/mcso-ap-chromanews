package com.mcso.ap.chromanews.model.api

import com.google.gson.annotations.SerializedName

class SentimentData (
    @SerializedName("type")
    val type: String,

    @SerializedName("score")
    val score: String
)