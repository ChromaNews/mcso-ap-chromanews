package com.mcso.ap.chromanews

import androidx.lifecycle.ViewModel
import com.mcso.ap.chromanews.model.auth.FirebaseUserLiveData

class MainViewModel(): ViewModel() {
    // Firebase
    private val firebaseAuthLiveData = FirebaseUserLiveData()

    // update firebase user
    fun updateUser(){
        firebaseAuthLiveData.updateUser()
    }
}