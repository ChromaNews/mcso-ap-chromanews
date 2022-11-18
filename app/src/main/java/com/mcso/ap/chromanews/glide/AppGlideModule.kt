package com.mcso.ap.chromanews

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


@GlideModule
class AppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.ERROR)
    }
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}

object Glide {
    private val width = Resources.getSystem().displayMetrics.widthPixels
    private val height = Resources.getSystem().displayMetrics.heightPixels
    private var glideOptions = RequestOptions ()
        .fitCenter()
        .transform(RoundedCorners (20))

    private fun fromHtml(source: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            @Suppress("DEPRECATION")
            return Html.fromHtml(source).toString()
        }
    }

    fun glideFetch(urlString: String?, thumbnailURL: String?, imageView: ImageView) {
            GlideApp.with(imageView.context)
                .asBitmap() // Try to display animated Gifs and video still
                .load(urlString?.let { fromHtml(it) })
                .apply(glideOptions)
                .error(R.color.colorAccent)
                .override(width, height)
                .error(
                    GlideApp.with(imageView.context)
                        .asBitmap()
                        .load(thumbnailURL?.let { fromHtml(it) })
                        .apply(glideOptions)
                        .error(R.color.colorAccent)
                        .override(500, 500)
                )
                .into(imageView)
        }
}
