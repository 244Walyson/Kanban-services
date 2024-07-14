package com.waly.chat.views

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.waly.chat.R
import com.waly.chat.databinding.ActivityWebViewGithubBinding

class WebViewGithubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewGithubBinding
    private lateinit var webView: WebView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewGithubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView

        val url = intent.getStringExtra("url")
        if(url == null) {
            finish()
        }

        webView.loadUrl(url!!)

        webView.webViewClient = WebViewClient()

        webView.settings.javaScriptEnabled = true

    }
}