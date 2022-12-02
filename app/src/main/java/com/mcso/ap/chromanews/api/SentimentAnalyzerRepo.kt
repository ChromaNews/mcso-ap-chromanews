package com.mcso.ap.chromanews.api

import android.util.Log
import com.mcso.ap.chromanews.model.api.SentimentData

class SentimentAnalyzerRepo(private val api: SentimentAnalyzerApi) {
    suspend fun analyzeNewsText(newsText: String): SentimentData {

        var response = SentimentData("neutral", "0.0")

        try{
            response = api.analyzeText(newsText)
        } catch (e: Exception){
            Log.e(javaClass.simpleName, "Exception while analyzing text [$newsText]: ${e.message}")
        }
        return response
    }
}