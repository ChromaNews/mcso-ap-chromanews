package com.mcso.ap.chromanews

import android.content.Context
import androidx.fragment.app.*
import androidx.fragment.app.Fragment

@Suppress("DEPRECATION")
internal class ViewPagerAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    val categoryFragment: String = "CategoryFragment"
    val newsFeedFragment: String = "NewsFeedFragment"
    val bookmarkFragment: String = "BookmarkFragment"
    val emptyFragment: String = "EmptyFragment"

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                CategoryFragment()
            }
            1 -> {
                NewsFeedFragment()
            }
            2 -> {
                BookmarkFragment()
            }
            else -> CategoryFragment()
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}