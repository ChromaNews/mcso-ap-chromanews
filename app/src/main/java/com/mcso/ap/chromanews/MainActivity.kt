package com.mcso.ap.chromanews

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mcso.ap.chromanews.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewpager: ViewPager
    private lateinit var simpleFrameLayout: FrameLayout
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

        tabLayout = findViewById(R.id.tab_layout)
        simpleFrameLayout =  findViewById(R.id.simpleFrameLayout)
        viewpager = findViewById(R.id.view_pager)
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = ViewPagerAdapter(this, supportFragmentManager,
            tabLayout.tabCount)
        viewpager.adapter = adapter
        // tabLayout.setupWithViewPager(viewpager)

        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d("ANBU: ", "onTabSelected")
                /*
                val selectCat = viewModel.getCategories().value
                if (selectCat != null) {
                    Log.d("ANBU: onTabSelected", selectCat.size.toString())
                    Log.d("ANBU: onTabSelected", selectCat.toString())
                }
                if (selectCat != null) {
                    if (selectCat.isEmpty()){
                        Log.d("ANBU: ", "Empty onTabSelected")
                        supportFragmentManager
                            .beginTransaction()
                            .add(R.id.recyclerRVView, EmptyFragment())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit()
                    }
                } */
                viewpager.currentItem = tab.position

                /*
                var fragment: Fragment? = null

                when (tab.position) {
                    0 -> fragment = CategoryFragment()
                    1 -> fragment = NewsFeedFragment()
                    2 -> fragment = BookmarkFragment()
                }

                if (fragment != null) {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.simpleFrameLayout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_OPEN)
                        .commit()
                }
                 */
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

}