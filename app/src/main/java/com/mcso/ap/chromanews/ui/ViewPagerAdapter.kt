package com.mcso.ap.chromanews.ui

import android.content.Context
import androidx.fragment.app.*
import androidx.fragment.app.Fragment
import com.mcso.ap.chromanews.ui.bookmark.BookmarkFragment
import com.mcso.ap.chromanews.ui.category.CategoryFragment
import com.mcso.ap.chromanews.ui.conflict.ConflictMapFragment
import com.mcso.ap.chromanews.ui.newsfeed.NewsFeedFragment
import com.mcso.ap.chromanews.ui.sentiment.MoodColorFragment

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
            3 -> {
                MoodColorFragment()
            }
            4 -> {
                ConflictMapFragment()
            }
            else -> CategoryFragment()
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}