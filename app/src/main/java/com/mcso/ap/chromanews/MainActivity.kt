package com.mcso.ap.chromanews

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.mcso.ap.chromanews.db.SentimentDBHelper

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val viewModel: MainViewModel by viewModels()

    // call back once log signInIntent is completed in AuthInit()
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.updateUser()
            } else {
                Log.e(TAG, "User login failed")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start firebase signIn
        AuthInit(viewModel,signInLauncher)

        // test newsdata
        viewModel.netNewsData()

        // test firestore
        viewModel.updateUserSentiment()
    }
}