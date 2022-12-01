package com.mcso.ap.chromanews.api

import android.util.Log
import com.mcso.ap.chromanews.model.conflict.Conflicts
import com.mcso.ap.chromanews.model.conflict.ConflictsResponse

class ConflictRepo(private val api: ConflictsApi) {
    suspend fun getConflictData(country: String): ConflictsResponse {

        var response = ConflictsResponse(0, 0, mutableListOf<Conflicts>())

        try {
            response = api.getConflictData(country)
        } catch (e: Exception){
            Log.e(javaClass.simpleName, "Exception while fetching conflict data: ${e.message}")
        }
        return response
    }
}