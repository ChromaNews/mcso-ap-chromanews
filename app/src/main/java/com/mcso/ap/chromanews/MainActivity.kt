package com.mcso.ap.chromanews

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.Switch
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
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
import com.mcso.ap.chromanews.model.Tabs
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
    private var switch_state = false

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
        tabLayout = findViewById(R.id.tab_layout)

        val viewPager2Adapter = ViewPagerAdapter(this)
        val fragmentList: ArrayList<Fragment> = ArrayList() //creates an ArrayList of Fragments

        Tabs.values().forEach { tabs ->
            run {
                titles.add(tabs.getTitle())
                fragmentList.add(tabs.getFragment())
            }
        }
        viewPager2Adapter.setData(fragmentList) //sets the data for the adapter

        viewPager2.adapter = viewPager2Adapter
        TabLayoutMediator(tabLayout, viewPager2, this).attach()


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d(TAG, "selected ${tab.text} at position ${tab.position}")
                var selectedTab = tab.text.toString()

                viewModel.setCategory(selectedTab)

                if (Tabs.SENTIMENT.getTitle() != selectedTab
                    && Tabs.CONFLICTS.getTitle() != selectedTab){

                    if (Tabs.BOOKMARK.getTitle() == selectedTab){
                        viewModel.fetchSavedNewsList()
                    } else {
                        viewModel.getFeedForCategory()
                    }
                }
            }
           override fun onTabUnselected(tab: TabLayout.Tab) {}
           override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        val item = menu!!.findItem(R.id.myswitch)
        item.setActionView(R.layout.use_switch)

        var rootView = findViewById<View>(android.R.id.content).rootView

        val mySwitch = item.actionView.findViewById<Switch>(R.id.switchAB)
        mySwitch.setOnCheckedChangeListener { _ , isChecked ->
            // do what you want with isChecked
            Log.d("ANBU: isChecked", isChecked.toString())
            Log.d("ANBU: switch_state", switch_state.toString())
            // val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            //when (isNightTheme) {
                if (isChecked) {
                    switch_state = true
                    Log.d("ANBU: switch_state", switch_state.toString())
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                    // delegate.applyDayNight()
                    // mySwitch.isChecked = true
                    // setTheme(R.style.DarkTheme)
                    // rootView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
                }else{
                    switch_state = false
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                    //delegate.applyDayNight()
                    // setTheme(R.style.DayLightTheme)
                    // rootView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
            }
        }

        return true
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = titles[position]
    }
}