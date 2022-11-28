package com.mcso.ap.chromanews

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
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
import com.mcso.ap.chromanews.model.MainViewModel
import com.mcso.ap.chromanews.ui.ViewPagerAdapter
import com.mcso.ap.chromanews.ui.bookmark.BookmarkFragment
import com.mcso.ap.chromanews.ui.newsfeed.*
import com.mcso.ap.chromanews.ui.conflict.ConflictMapFragment
import com.mcso.ap.chromanews.ui.sentiment.MoodColorFragment

class MainActivity : AppCompatActivity(), TabLayoutMediator.TabConfigurationStrategy
{
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var actionBarBinding: ActionBarBinding? = null
    private val titles = ArrayList<String>()

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

        // setSupportActionBar(binding.toolbarMain)

        // Firebase Auth
        AuthInit(viewModel,signInLauncher)

        viewPager2 = findViewById(R.id.view_pager)
        viewPager2.offscreenPageLimit = 1
        tabLayout = findViewById(R.id.tab_layout)

        titles.add("Business")
        titles.add("Entertainment")
        titles.add("Health")
        titles.add("Science")
        titles.add("Sports")
        titles.add("Technology")
        titles.add("Bookmarks")
        titles.add("Sentiment")
        titles.add("Conflicts")

        val viewPager2Adapter = ViewPagerAdapter(this)
        val fragmentList: ArrayList<Fragment> = ArrayList() //creates an ArrayList of Fragments

        fragmentList.add(BusinessFragment())
        fragmentList.add(EntertainmentFragment())
        fragmentList.add(HealthFragment())
        fragmentList.add(ScienceFragment())
        fragmentList.add(SportsFragment())
        fragmentList.add(TechnologyFragment())
        fragmentList.add(BookmarkFragment())
        fragmentList.add(MoodColorFragment())
        fragmentList.add(ConflictMapFragment())
        viewPager2Adapter.setData(fragmentList) //sets the data for the adapter

        viewPager2.adapter = viewPager2Adapter
        TabLayoutMediator(tabLayout, viewPager2, this).attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d(TAG, "selected ${tab.text} at position ${tab.position}")
                var selectedTab = tab.text.toString().lowercase(Locale.ROOT)

                viewModel.setCategory(selectedTab)
                viewModel.getFeedForCategory()

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