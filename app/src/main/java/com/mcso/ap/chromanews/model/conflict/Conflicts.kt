package com.mcso.ap.chromanews.model.conflict

import com.google.gson.annotations.SerializedName

data class Conflicts(
    @SerializedName("location")
    val location: String,

    @SerializedName("latitude")
    val latitude: String,

    @SerializedName("longitude")
    val longitude: String
)
