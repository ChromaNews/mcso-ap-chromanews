package com.mcso.ap.chromanews.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mcso.ap.chromanews.R
import com.mcso.ap.chromanews.databinding.ActivityReadNewsBinding
import com.mcso.ap.chromanews.model.MainViewModel


class ReadNews : AppCompatActivity() {

    private lateinit var newsWebView: WebView
    private lateinit var progressBar: ProgressBar
    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var activityReadNewsBinding = ActivityReadNewsBinding.inflate(layoutInflater)

        setContentView(activityReadNewsBinding.root)

        setSupportActionBar(activityReadNewsBinding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        val bundle: Bundle? = intent.extras

        newsWebView = findViewById(R.id.news_webview)
        progressBar = findViewById(R.id.progressBar)

        progressBar.max = 50

        newsWebView.apply {
            settings.apply {
                domStorageEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadsImagesAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                javaScriptEnabled = true
            }
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
        }

        newsWebView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        progressBar.progress = 0

        if (bundle?.getString("linkKey") != null) {
            newsWebView.loadUrl(bundle?.getString("linkKey")!!)
        }

        // Analyze news sentiment
        bundle?.getString("titleKey")?.let {
            Log.d("TAG", "analyzing news: ${bundle?.getString("titleKey")}")
            viewModel.netAnalyzeNews(it)
        }

        // update sentiment to DB
        viewModel.observeSentimentScore().observe(this) { sentimentData ->
            run {
                val score = String.format(
                    "%.6f", sentimentData.score.toDouble()
                ).toDouble()
                viewModel.updateUserSentiment(score)
            }
        }

        newsWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, newUrl: String): Boolean {
                view.loadUrl(newUrl)
                progressBar.progress = 0
                return true
            }

            override fun onPageFinished(view: WebView, urlPage: String) {
                progressBar.visibility = View.GONE
                invalidateOptionsMenu()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
