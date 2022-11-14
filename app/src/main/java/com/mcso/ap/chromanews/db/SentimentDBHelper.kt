package com.mcso.ap.chromanews.db

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mcso.ap.chromanews.model.sentiment.RatingDate
import com.mcso.ap.chromanews.model.sentiment.UserSentimentData
import okhttp3.internal.toImmutableList
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SentimentDBHelper {
    private val TAG = "SentimentDBHelper"

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "userSentiments"
    private val dateCollection = "dateList"

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
                                Log.d(TAG, "Error while creating sentiment : ${it.stackTrace}")
                            }
                    }
                }
            }
    }

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

    private fun updateRatingToTodayDoc(todayRatingDocument: DocumentReference, ratingDate: RatingDate){
        todayRatingDocument.set(ratingDate)
            .addOnSuccessListener {
                Log.d(TAG, "successfully added rating to list on ${ratingDate.date}")
            }
            .addOnFailureListener {
                Log.e(TAG, "Error while adding rate for date: $ratingDate.date: ${it.stackTrace}")
            }
    }

    private fun createDateCollection(dateCollectionRef: CollectionReference, dateList: List<RatingDate>){
        dateList.forEach { ratingDate ->
            run {
                dateCollectionRef.document(ratingDate.date).set(ratingDate)
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully added ${ratingDate.date}")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Error while adding ${ratingDate.date}")
                    }
            }
        }
    }
}