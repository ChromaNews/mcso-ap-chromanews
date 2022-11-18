package com.mcso.ap.chromanews.api

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.text.clearSpans
import com.google.gson.annotations.SerializedName

data class NewsPost (
    @SerializedName("title")
    val title: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("keywords")
    val keywords: List<String>? = null,
    @SerializedName("creator")
    val creator: List<String>? = null,
    @SerializedName("video_url")
    val videoURL: String?,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("pubDate")
    val pubDate : String,
    @SerializedName("image_url")
    val imageURL : String?,
    @SerializedName("source_id")
    val sourceId: String,
    @SerializedName("country")
    val country: List<String>,
    @SerializedName("category")
    val category: List<String>,
    @SerializedName("language")
    val language: String
) {
    companion object {
        // NB: This only highlights the first match in a string
        private fun findAndSetSpan(fulltext: SpannableString, subtext: String): Boolean {
            if (subtext.isEmpty()) return true
            val i = fulltext.indexOf(subtext, ignoreCase = true)
            if (i == -1) return false
            fulltext.setSpan(
                ForegroundColorSpan(Color.CYAN), i, i + subtext.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return true
        }

        fun spannableStringsEqual(a: SpannableString?, b: SpannableString?): Boolean {
            if(a == null && b == null) return true
            if(a == null && b != null) return false
            if(a != null && b == null) return false
            val spA = a!!.getSpans(0, a.length, Any::class.java)
            val spB = b!!.getSpans(0, b.length, Any::class.java)
            return a.toString() == b.toString()
                    &&
                    spA.size == spB.size && spA.equals(spB)

        }
    }
    private fun clearSpan(str: SpannableString?) {
        str?.clearSpans()
        str?.setSpan(
            ForegroundColorSpan(Color.GRAY), 0, 0,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    // clearSpans does not invalidate the textview
    // We have to assign a span to make sure text gets redrawn, so assign
    // a span that does nothing
    private fun removeAllCurrentSpans(){
        // Erase all spans
        // clearSpan(title)
        //clearSpan(description)
        // clearSpan(content)
        // clearSpan(category)
    }

    // Given a search string, look for it in the RedditPost.  If found,
    // highlight it and return true, otherwise return false.
    fun searchFor(searchTerm: String): Boolean {
        // XXX Write me, search both regular posts and subreddit listings

        return true
    }

    // NB: This changes the behavior of lists of RedditPosts.  I want posts fetched
    // at two different times to compare as equal.  By default, they will be different
    // objects with different hash codes.
    override fun equals(other: Any?) : Boolean =
        if (other is NewsPost) {
            title == other.title
        } else {
            false
        }
}
