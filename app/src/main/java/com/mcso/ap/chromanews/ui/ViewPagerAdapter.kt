package com.mcso.ap.chromanews.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity?) :
    FragmentStateAdapter(fragmentActivity!!) {

    private var fragments //variable holds the fragments the ViewPager2 allows us to swipe to.
            : ArrayList<Fragment>? = null

    override fun createFragment(position: Int): Fragment {
        return fragments!![position]
    }

    override fun getItemCount(): Int {
        return fragments!!.size
    }

    fun setData(fragments: ArrayList<Fragment>?) {
        this.fragments = fragments
    }
}