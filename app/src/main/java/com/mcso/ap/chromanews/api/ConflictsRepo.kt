package com.mcso.ap.chromanews.api

import android.util.Log
import com.mcso.ap.chromanews.model.conflict.Conflicts
import com.mcso.ap.chromanews.model.conflict.ConflictsResponse
import java.util.*

class ConflictRepo(private val api: ConflictsApi) {
    suspend fun getConflictData(country: String): ConflictsResponse {

        var response = ConflictsResponse(0, 0, mutableListOf<Conflicts>())

        try {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Log.d(javaClass.simpleName, "Fetching conflicts in $country for $currentYear")
            response = api.getConflictData(country, currentYear)
        } catch (e: Exception){
            Log.e(javaClass.simpleName, "Exception while fetching conflict data: ${e.message}")
        }
        return response
    }
}