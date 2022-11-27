package com.mcso.ap.chromanews

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mcso.ap.chromanews.databinding.ActionBarBinding
import com.mcso.ap.chromanews.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList


// class MainActivity : AppCompatActivity() {
class MainActivity : AppCompatActivity(), TabLayoutMediator.TabConfigurationStrategy
{

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var simpleFrameLayout: FrameLayout
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private var actionBarBinding: ActionBarBinding? = null
    private val titles = ArrayList<String>()

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

        // test firestore
        // viewModel.calculateRating()

        // viewModel.observeRatingByDate().observe(this){
        //    viewModel.calculateSentimentColorCode(it)
        // }

        /*
        tabLayout = findViewById(R.id.tab_layout)
        simpleFrameLayout =  findViewById(R.id.simpleFrameLayout)
        viewpager = findViewById(R.id.view_pager)
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = ViewPagerAdapter(this, supportFragmentManager,
            tabLayout.tabCount)
        viewpager.adapter = adapter
        // tabLayout.setupWithViewPager(viewpager)
         */


        viewPager2 = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        titles.add("Business")
        titles.add("Entertainment")
        // titles.add("General")
        titles.add("Health")
        titles.add("Science")
        titles.add("Sports")
        titles.add("Technology")
        titles.add("Bookmarks")

        val viewPager2Adapter = ViewPagerAdapter(this)
        val fragmentList: ArrayList<Fragment> = ArrayList() //creates an ArrayList of Fragments

        fragmentList.add(BusinessFragment())
        fragmentList.add(EntertainmentFragment())
        // fragmentList.add(GeneralFragment())
        fragmentList.add(HealthFragment())
        fragmentList.add(ScienceFragment())
        fragmentList.add(SportsFragment())
        fragmentList.add(TechnologyFragment())
        fragmentList.add(BookmarkFragment())
        viewPager2Adapter.setData(fragmentList) //sets the data for the adapter

        viewPager2.adapter = viewPager2Adapter
        TabLayoutMediator(tabLayout, viewPager2, this).attach()

        // viewPager2.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d("ANBU: ", "onTabSelected")
                Log.d("ANBU: tab position", tab.position.toString())
                Log.d("ANBU: fragment tab position", tab.text.toString())
                var selectedTab = tab.text.toString().lowercase(Locale.ROOT)
                viewModel.setCategory(selectedTab)

                if (selectedTab == "bookmarks"){
                    viewModel.fetchSavedNewsList()
                }
            }
           override fun onTabUnselected(tab: TabLayout.Tab) {}
           override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = titles[position]
    }
}