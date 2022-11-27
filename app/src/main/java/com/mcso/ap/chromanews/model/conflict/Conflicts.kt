package com.mcso.ap.chromanews.model.conflict

import com.google.gson.annotations.SerializedName

data class Conflicts(
    @SerializedName("location")
    val location: String,

    @SerializedName("latitude")
    val latitude: String,

    @SerializedName("longitude")
    val longitude: String,

    @SerializedName("notes")
    val notes: String,

    @SerializedName("source")
    val source: String,

    @SerializedName("event_date")
    val date: String,

    @SerializedName("actor1")
    val actor_one: String,

    @SerializedName("actor2")
    val actor_two: String
)
