package com.mcso.ap.chromanews.model.conflict

import com.google.gson.annotations.SerializedName

data class ConflictsResponse (
    @SerializedName("status")
    val status: Int,

    @SerializedName("count")
    val count: Int,

    @SerializedName("data")
    val conflictList: MutableList<Conflicts>
)