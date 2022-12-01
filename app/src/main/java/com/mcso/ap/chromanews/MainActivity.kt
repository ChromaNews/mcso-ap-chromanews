package com.mcso.ap.chromanews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mcso.ap.chromanews.databinding.ActionBarBinding
import com.mcso.ap.chromanews.databinding.ActivityMainBinding
import kotlin.collections.ArrayList
import com.mcso.ap.chromanews.model.MainViewModel
import com.mcso.ap.chromanews.model.Tabs
import com.mcso.ap.chromanews.ui.ViewPagerAdapter

class MainActivity : AppCompatActivity(), TabLayoutMediator.TabConfigurationStrategy
{
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var binding: ActivityMainBinding
    private var actionBarBinding: ActionBarBinding? = null
    private val viewModel: MainViewModel by viewModels()
    private val titles = ArrayList<String>()

    companion object {
        private val TAG = "MainActivity"
    }

    // call back once log signInIntent is completed in AuthInit()
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.updateUser()
            } else {
                Log.e(MainActivity.TAG, "User login failed")
            }
        }
    }

    private fun initActionBar(actionBar: ActionBar) {
        // Disable the default and enable the custom
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)

        actionBarBinding = ActionBarBinding.inflate(layoutInflater)
        // Apply the custom view
        actionBar.customView = actionBarBinding?.root
        actionBarBinding!!.actionSearch.isVisible = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        actionBarBinding = ActionBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.let{
            initActionBar(it)
        }

        // Firebase Auth
        AuthInit(viewModel,signInLauncher)

        val logoutButton: FloatingActionButton = binding.logout

        logoutButton.setOnClickListener {
            showDialog()
        }

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

                actionBarBinding!!.actionSearch.setText("")

                viewModel.setCategory(selectedTab)
                actionBarBinding!!.actionSearch.isVisible = true

                if (Tabs.SENTIMENT.getTitle() == selectedTab
                    || Tabs.CONFLICTS.getTitle() == selectedTab){
                    actionBarBinding!!.actionSearch.isVisible = false
                }

                if (Tabs.SENTIMENT.getTitle() != selectedTab
                    && Tabs.CONFLICTS.getTitle() != selectedTab){

                    if (Tabs.BOOKMARK.getTitle() == selectedTab){
                        viewModel.fetchSavedNewsList()
                        actionBarBinding!!.actionSearch.isVisible = false
                    } else {
                        viewModel.getFeedForCategory()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        actionBarBinding?.actionSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) hideKeyboard()
                viewModel.setSearchTerm(s.toString())
            }
        })

        // trigger sentiment rate calculation once user logs on
        viewModel.observeUserName().observe(this){
            if (viewModel.getCurrentUser() != null){
                viewModel.calculateRating()
            }
        }
    }

    private fun showDialog(){
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Are you sure you want to logout?")
        alertDialog.setPositiveButton("yes") { _, _ ->
            Toast.makeText(this, "Logging out...", Toast.LENGTH_LONG).show()

            viewModel.logoutUser()
            AuthInit(viewModel,signInLauncher)

            // clear activity that triggers in launching AuthUI
            val intent = Intent(this, MainActivity::class.java);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        alertDialog.setNegativeButton("No") { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = titles[position]
    }

    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }
}