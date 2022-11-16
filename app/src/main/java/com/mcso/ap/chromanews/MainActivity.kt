package com.mcso.ap.chromanews

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcso.ap.chromanews.api.Repository
import com.mcso.ap.chromanews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var dataList = Repository.fetchData()
    private lateinit var  adapter: CategoryAdapter
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView

    companion object {
        private val TAG = "MainActivity"
        private const val mainFragTag = "mainFragTag"
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

        // Start firebase signIn
        AuthInit(viewModel,signInLauncher)

        // added data from arraylist to adapter class.
        // recyclerView = binding.recyclerView
        // recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
        // adapter = CategoryAdapter(viewModel)
        // recyclerView.adapter = adapter

        // adapter.setDataList(dataList)

        // test newsdata
        // viewModel.netNewsData()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.recyclerView)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_categories, R.id.navigation_newsfeed, R.id.navigation_bookmark
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // Navigation sets the title to "Simple"
        supportActionBar?.title = "Categories"
    }
}