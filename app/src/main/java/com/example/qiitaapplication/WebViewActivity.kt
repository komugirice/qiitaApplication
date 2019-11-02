package com.example.qiitaapplication

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val myWebView = findViewById<WebView>(R.id.webView);
        myWebView.setWebViewClient(WebViewClient())
        val url = intent.getStringExtra("url")
        myWebView.loadUrl(url)
    }
}
