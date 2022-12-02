package com.mcso.ap.chromanews.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mcso.ap.chromanews.model.savedNews.NewsMetaData
import com.mcso.ap.chromanews.model.sentiment.UserSentimentData

class NewsDBHelper {
    private val TAG = "NewsDBHelper"

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "bookmarkNews"
    private val newsCollection = "newsList"

    /**
     * Fetch news metadata collection
     */
    fun fetchSavedNews(email: String,
                       newsList: MutableLiveData<List<NewsMetaData>>) {
        dbFetchSavedNews(email, newsList)
    }
    private fun limitAndGet(query: Query, newsList: MutableLiveData<List<NewsMetaData>>) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "allNotes fetch ${result!!.documents.size}")
                newsList.postValue(result.documents.mapNotNull {
                    it.toObject(NewsMetaData::class.java)
                })
            }
            .addOnFailureListener {
                Log.e(TAG, "allNotes fetch FAILED ", it)
            }
    }

    private fun dbFetchSavedNews(email: String,
                                 newsList: MutableLiveData<List<NewsMetaData>>) {
        val sortColumn = "newsID"
        val sortBy = Query.Direction.ASCENDING
        val query = db.collection(rootCollection).document(email).collection(newsCollection)
            .orderBy(sortColumn, sortBy)
        limitAndGet(query, newsList)
    }

    /**
     * Create email if email doesn't exist. If exists, use it for saving metadata
     */
    private fun createNewsMetadataUser(email: String){
        db.collection(rootCollection).document(email)
            .get()
            .addOnSuccessListener { doc ->
                run {
                    if (doc.exists()) {
                        Log.d(TAG, "User: $email exists")
                    } else {
                        val user = UserSentimentData(email)
                        db.collection(rootCollection).document(email).set(user)
                            .addOnSuccessListener {
                                Log.d(TAG, "Successfully added $email")
                            }
                            .addOnFailureListener {
                                Log.e(TAG, "Error while create news : ${it.message}")
                            }
                    }
                }
            }
    }

    /**
     * Create news metadata collection
     */
    fun createNewsMetadata(email: String,
                newsMeta: NewsMetaData,
                newsList: MutableLiveData<List<NewsMetaData>>
    )
    {
        createNewsMetadataUser(email)

        db.collection(rootCollection)
            .document(email).collection(newsCollection)
                .add(newsMeta)
                .addOnSuccessListener {
                    Log.d(
                        javaClass.simpleName, "createNewsMetadata is executed successfully"
                    )
                    fetchSavedNews(email, newsList)
                }
                .addOnFailureListener { e ->
                    Log.w(javaClass.simpleName, "Error in creating news metadata ", e)
                }
    }

    /**
     * Remove news metadata collection
     */
    fun removeNewsMetadata(email: String,
        newsMeta: NewsMetaData,
        newsList: MutableLiveData<List<NewsMetaData>>
    ) {
        db.collection(rootCollection)
            .document(email).collection(newsCollection)
            .document(newsMeta.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName, "RemoveNewsMetadata is executed successfully"
                )
                fetchSavedNews(email, newsList)
            }
            .addOnFailureListener { e ->
                Log.w(javaClass.simpleName, "Error in removing news metadata", e)
            }
    }
}
