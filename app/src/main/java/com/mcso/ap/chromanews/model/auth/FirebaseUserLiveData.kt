package com.mcso.ap.chromanews.model.auth

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData : LiveData<FirebaseUser>() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val authStateListener = FirebaseAuth.AuthStateListener {
        value = firebaseAuth.currentUser
    }

    fun updateUser(){
        value = firebaseAuth.currentUser
    }

    fun getUser(): String? {
        return firebaseAuth.currentUser?.email
    }


}