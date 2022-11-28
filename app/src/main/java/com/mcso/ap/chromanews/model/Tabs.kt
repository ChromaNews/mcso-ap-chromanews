package com.mcso.ap.chromanews.model

import androidx.fragment.app.Fragment
import com.mcso.ap.chromanews.ui.bookmark.BookmarkFragment
import com.mcso.ap.chromanews.ui.conflict.ConflictMapFragment
import com.mcso.ap.chromanews.ui.newsfeed.*
import com.mcso.ap.chromanews.ui.sentiment.MoodColorFragment

enum class Tabs {

    SENTIMENT{
        override fun getTitle(): String {
            return "Sentiment"
        }

        override fun getFragment(): Fragment {
            return MoodColorFragment()
        }
    },

    CONFLICTS{
        override fun getTitle(): String {
            return "Conflicts"
        }

        override fun getFragment(): Fragment {
            return ConflictMapFragment()
        }
    },

    BUSINESS{
        override fun getTitle(): String {
            return "Business"
        }

        override fun getFragment(): Fragment {
            return BusinessFragment()
        }

        val hasNewsFeed = true
    },

    ENTERTAINMENT{
        override fun getTitle(): String {
            return "Entertainment"
        }

        override fun getFragment(): Fragment {
            return EntertainmentFragment()
        }
    },

    HEALTH{
        override fun getTitle(): String {
            return "Health"
        }

        override fun getFragment(): Fragment {
            return HealthFragment()
        }
    },

    SCIENCE{
        override fun getTitle(): String {
            return "Science"
        }

        override fun getFragment(): Fragment {
            return ScienceFragment()
        }
    },

    SPORTS{
        override fun getTitle(): String {
            return "Sports"
        }

        override fun getFragment(): Fragment {
            return SportsFragment()
        }
    },

    TECHNOLOGY{
        override fun getTitle(): String {
            return "Technology"
        }

        override fun getFragment(): Fragment {
            return TechnologyFragment()
        }
    },

    BOOKMARK{
        override fun getTitle(): String {
            return "Bookmarks"
        }

        override fun getFragment(): Fragment {
            return BookmarkFragment()
        }
    };

    abstract fun getTitle(): String
    abstract fun getFragment(): Fragment
}