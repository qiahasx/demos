package com.example.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import com.example.common.util.debug

class WebActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(KEY_URL) ?: return
        debug(url)
        val webView = WebView(this)
        webView.settings.apply {
            cacheMode = WebSettings.LOAD_DEFAULT
            domStorageEnabled = true
            javaScriptEnabled = true
        }
        setContentView(webView)
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
        onBackPressedDispatcher.addCallback {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                finish()
            }
        }
    }

    companion object {
        private const val KEY_URL = "url"
        fun Context.navWeb(url: String) {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(KEY_URL, url)
            startActivity(intent)
        }
    }
}