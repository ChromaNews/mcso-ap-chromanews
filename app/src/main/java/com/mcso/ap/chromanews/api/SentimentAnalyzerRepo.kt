package com.mcso.ap.chromanews.api

import com.mcso.ap.chromanews.model.api.SentimentData

class SentimentAnalyzerRepo(private val api: SentimentAnalyzerApi) {
    suspend fun analyzeNewsText(newsText: String): SentimentData {
        return api.analyzeText(newsText)
    }
}