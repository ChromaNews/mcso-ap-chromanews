package com.mcso.ap.chromanews.api

import com.mcso.ap.chromanews.model.conflict.ConflictsResponse

class ConflictRepo(private val api: ConflictsApi) {
    suspend fun getConflictData(country: String): ConflictsResponse {
        return api.getConflictData(country)
    }
}