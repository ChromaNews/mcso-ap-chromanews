package com.mcso.ap.chromanews.model.api

import com.google.gson.annotations.SerializedName

data class DataNews (
    @SerializedName("category")
    val category: String,
    @SerializedName("type")
    // boolean, multiple choice
    val type: String,
    @SerializedName("difficulty")
    // easy, medium, hard
    val difficulty: String,
    @SerializedName("question")
    val question: String,
    // XXX Write me (one field)
    @SerializedName("correct_answer")
    val correctAnswer: String,
    @SerializedName("incorrect_answers")
    val incorrectAnswers: List<String>

)