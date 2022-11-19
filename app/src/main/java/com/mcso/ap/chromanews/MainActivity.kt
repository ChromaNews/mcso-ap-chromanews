package com.mcso.ap.chromanews

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mcso.ap.chromanews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView

    companion object {
        private val TAG = "MainActivity"
        private const val mainFragTag = "mainFragTag"
        // private val category = "entertainment"
    }

    // call back once log signInIntent is completed in AuthInit()
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                // viewModel.updateUser()
            } else {
                Log.e(MainActivity.TAG, "User login failed")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setSupportActionBar(binding.toolbarMain)

        // Firebase Auth
        AuthInit(viewModel,signInLauncher)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.NavHostFragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_categories, R.id.navigation_newsfeed, R.id.navigation_bookmark
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}