package com.mcso.ap.chromanews.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
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

    fun getPageTitle(position: Int): Fragment? {
        // return fragments?.get(position)?.arguments?.getString("title")
        return fragments?.get(position)
    }

    fun getItemPosition(`object`: Any?): Int {
        return PagerAdapter.POSITION_NONE
    }
}