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
    @SerializedName("url")
    val link: String,
    @SerializedName("author")
    val author: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("publishedAt")
    val pubDate : String,
    @SerializedName("urlToImage")
    val imageURL : String?,
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

    override fun equals(other: Any?) : Boolean =
        if (other is NewsPost) {
            title == other.title
        } else {
            false
        }
}
