package com.mcso.ap.chromanews.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mcso.ap.chromanews.model.sentiment.RatingDate
import com.mcso.ap.chromanews.model.sentiment.UserSentimentData
import okhttp3.internal.toImmutableList
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SentimentDBHelper {
    private val TAG = "SentimentDBHelper"

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "userSentiments"
    private val dateCollection = "dateList"

    /**
     * Create user sentiment collection
     */
    private fun createSentimentUser(email: String){
        db.collection(rootCollection).document(email)
            .get()
            .addOnSuccessListener { doc ->
                run {
                    if (doc.exists()) {
                        Log.d(TAG, "User: $email exists")
                    } else {
                        val userSentiment = UserSentimentData(email)
                        db.collection(rootCollection).document(email).set(userSentiment)
                            .addOnSuccessListener {
                                Log.d(TAG, "Successfully added $email")
                            }
                            .addOnFailureListener {
                                Log.e(TAG, "Error while creating sentiment : ${it.message}")
                            }
                    }
                }
            }
    }

    /**
     * Fetch existing ratings, if exists and add a new rating entry
     */
    fun createSentimentRating(email: String, sentimentRating: Double){
        createSentimentUser(email)

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val todayRatingDocument = db.collection(rootCollection)
            .document(email).collection(dateCollection)
            .document(today.toString())

        todayRatingDocument.get().addOnSuccessListener { doc ->
            run {
                if (doc.exists()){
                    val ratingDate = doc.toObject(RatingDate::class.java)
                    Log.d(TAG, "retrieved ${ratingDate?.rateList?.size} rating(s) for date: ${ratingDate?.date}")
                    val newRateList = ratingDate?.rateList?.toMutableList()
                    newRateList?.add(sentimentRating)
                    Log.d(TAG, "rateList size after adding sentiment rating: ${newRateList?.size}")
                    ratingDate?.rateList = newRateList!!.toImmutableList()

                    // add to doc
                    updateRatingToTodayDoc(todayRatingDocument, ratingDate)
                } else {
                    Log.d(TAG, "Rating does not exist for $today. Creating...")
                    val ratingDate = RatingDate(today.toString(), mutableListOf(sentimentRating))
                    val dateCollectionRef = db.collection(rootCollection).document(email).collection(dateCollection)
                    createDateCollection(dateCollectionRef, mutableListOf(ratingDate))
                }
            }
        }
    }

    /**
     * add a new rating document for the current date
     */
    private fun updateRatingToTodayDoc(todayRatingDocument: DocumentReference, ratingDate: RatingDate){
        todayRatingDocument.set(ratingDate)
            .addOnSuccessListener {
                Log.d(TAG, "successfully added rating to list on ${ratingDate.date}")
            }
            .addOnFailureListener {
                Log.e(TAG, "Error while adding rate for date: $ratingDate.date: ${it.message}")
            }
    }

    /**
     * creates a date document in the date collection
     */
    private fun createDateCollection(dateCollectionRef: CollectionReference, dateList: List<RatingDate>){
        dateList.forEach { ratingDate ->
            run {
                dateCollectionRef.document(ratingDate.date).set(ratingDate)
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully added ${ratingDate.date}")
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "Error while adding ${ratingDate.date}: ${it.message}")
                    }
            }
        }
    }

    /**
     * returns a list of rating by date
     */
    fun getTotalRating(email: String, ratingDateList: MutableLiveData<List<Double>>) {
        val ratingByDate = mutableListOf<Double>()
        val dateCollection = db.collection(rootCollection)
            .document(email).collection(dateCollection)

        dateCollection.get()
            .addOnSuccessListener {

                Log.d(TAG, "result: ${it.documents.size}")
                it.documents.forEach { doc ->
                    run {
                        var totalRate: Double = 0.0
                        if (doc.exists()) {
                            val ratingDate = doc.toObject(RatingDate::class.java)
                            ratingDate?.rateList?.forEach{
                                    rate ->
                                run {
                                    totalRate += rate
                                }
                            }
                            totalRate /= ratingDate?.rateList?.size!!
                            ratingByDate.add(totalRate)
                        }
                    }
                }

                // default to neutral if there is no rating data available for the user
                if (ratingByDate.size > 0){
                    Log.d(TAG, "total rate: ${ratingByDate[0]}")
                    ratingDateList.postValue(ratingByDate)
                } else {
                    ratingDateList.postValue(mutableListOf(0.0))
                }
            }
    }
}