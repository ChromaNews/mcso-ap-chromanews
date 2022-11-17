package com.mcso.ap.chromanews


import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.mcso.ap.chromanews.databinding.ActivityOnePostBinding


class OneNewsPost : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityOnePostBinding = ActivityOnePostBinding.inflate(layoutInflater)
        setContentView(activityOnePostBinding.root)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        setSupportActionBar(activityOnePostBinding.toolbar)
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // supportActionBar?.setDisplayShowCustomEnabled(false)


        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val bundle: Bundle? = intent.extras
        // activityOnePostBinding.toolbar.title = ""

        var title = bundle?.getString("titleKey")
        var ellipTitle = title
        if (title!!.length > 30){
            // activityOnePostBinding.toolbarTitle.maxEms = 30
            // activityOnePostBinding.toolbarTitle.ellipsize = TextUtils.TruncateAt.END
            // activityOnePostBinding.toolbarTitle.text = title
            ellipTitle = title.substring(0, 30)+"\u2026"
        }

        activityOnePostBinding.toolbarTitle.text = ellipTitle

        //activityOnePostBinding.toolbarTitle.text = bundle?.getString("titleKey")
        // activityOnePostBinding.toolbarTitle.text = title
        var oneTextView = activityOnePostBinding.onePostText
        oneTextView.movementMethod = ScrollingMovementMethod()
        activityOnePostBinding.onePostText.text = bundle?.getString("selfTextKey")
        var img = bundle?.getString("imageKey")
        var thumbnail = bundle?.getString("thumbnailKey")
        Glide.glideFetch(img.toString(), thumbnail.toString(),
            activityOnePostBinding.onePostImage)
    }
    }
