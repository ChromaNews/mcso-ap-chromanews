package com.mcso.ap.chromanews


import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mcso.ap.chromanews.databinding.ActivityOnePostBinding


class OneNewsPost : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityOnePostBinding = ActivityOnePostBinding.inflate(layoutInflater)
        setContentView(activityOnePostBinding.root)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        // setSupportActionBar(activityOnePostBinding.toolbar)

        activityOnePostBinding.toolbar.title = "One Post"
        // supportActionBar?.setDisplayShowTitleEnabled(true)
        // supportActionBar?.setDisplayShowTitleEnabled(false)
        // supportActionBar?.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras

        var title = bundle?.getString("titleKey")
        //var ellipTitle = title
        //if (title!!.length > 30){
        //    ellipTitle = title.substring(0, 30)+"\u2026"
        //}

        // activityOnePostBinding.toolbarTitle.text = ellipTitle

        var oneTextView = activityOnePostBinding.onePostDesc
        oneTextView.movementMethod = ScrollingMovementMethod()
        activityOnePostBinding.onePostDesc.text = bundle?.getString("descKey")
        var img = bundle?.getString("imageKey")
        var author = bundle?.getString("authorKey")
        if (author != null) {
            if (author.isNotEmpty()){
                activityOnePostBinding.onePostAuthor.text = bundle?.getString("authorKey")
            }
        }
        activityOnePostBinding.onePostTitle.text = bundle?.getString("titleKey")
        activityOnePostBinding.onePostLink.text = bundle?.getString("linkKey")
        activityOnePostBinding.onePostAuthor.text = bundle?.getString("authorKey")
        Glide.glideFetch(img.toString(), null,
            activityOnePostBinding.onePostImage)
        }
}
