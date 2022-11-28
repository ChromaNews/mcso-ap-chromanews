package com.mcso.ap.chromanews.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mcso.ap.chromanews.model.savedNews.NewsMetaData


class NewsDBHelper {
    private val TAG = "NewsDBHelper"

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "bookmarkNews"

    fun fetchSavedNews(newsList: MutableLiveData<List<NewsMetaData>>) {
        dbFetchSavedNews(newsList)
    }
    private fun limitAndGet(query: Query, newsList: MutableLiveData<List<NewsMetaData>>) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allNotes fetch ${result!!.documents.size}")
                newsList.postValue(result.documents.mapNotNull {
                    it.toObject(NewsMetaData::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allNotes fetch FAILED ", it)
            }
    }

    private fun dbFetchSavedNews(newsList: MutableLiveData<List<NewsMetaData>>) {
        var sortColumn = "newsID"

        var sortby = Query.Direction.ASCENDING

        var query = db.collection(rootCollection)
            .orderBy(sortColumn, sortby)

        limitAndGet(query, newsList)
    }

    fun createNewsMetadata(
        newsMeta: NewsMetaData,
        newsList: MutableLiveData<List<NewsMetaData>>
    ) {
        Log.d("ANBU: ","calling inside createNewsMetadata dbhelper ")
        db.collection(rootCollection)
            .add(newsMeta)
            .addOnSuccessListener {
                Log.d(
                    //  javaClass.simpleName,
                    //  "Note create \"${elipsizeString(note.text)}\" id: ${note.firestoreID}"
                    "ANBU: ", "calling Create createNewsMetadata addOnSuccessListener"
                )
                fetchSavedNews(newsList)
            }
            .addOnFailureListener { e ->
                //  Log.d(javaClass.simpleName, "Note create FAILED \"${elipsizeString(note.text)}\"")
                Log.d("ANBU: ", "calling Create createNewsMetadata addOnFailureListener")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun removeNewsMetadata(
        newsMeta: NewsMetaData,
        newsList: MutableLiveData<List<NewsMetaData>>
    ) {
        db.collection(rootCollection)
            .document(newsMeta.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    "ANBU: ", "calling Remove removeNewsMetadata addOnSuccessListener"
                )
                fetchSavedNews(newsList)
            }
            .addOnFailureListener { e ->
                Log.d("ANBU: ", "calling Remove removeNewsMetadata addOnFailureListener")
                Log.w(javaClass.simpleName, "Error removing news Meta", e)
            }
    }
}
