package com.mcso.ap.chromanews

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.mcso.ap.chromanews.model.MainViewModel

class AuthInit(viewModel: MainViewModel, signInLauncher: ActivityResultLauncher<Intent>) {
    companion object{
        private const val TAG = "AuthInit"
    }

    init {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null){
            Log.d(TAG, "User is not registered")
            val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

            val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            signInLauncher.launch(signInIntent)
        } else {
            Log.d(TAG, "User: ${user.displayName}")
            viewModel.updateUser()
            viewModel.bookmarkListEmpty()
        }
    }
}