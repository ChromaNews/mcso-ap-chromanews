package com.mcso.ap.chromanews

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val viewModel: MainViewModel by viewModels()
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.updateUser()
            } else {
                Log.d(TAG, "User login failed")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AuthInit(viewModel,signInLauncher)
    }
}