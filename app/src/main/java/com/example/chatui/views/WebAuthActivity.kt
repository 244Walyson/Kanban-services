package com.example.chatui.views

import LoginResponse
import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.chatui.databinding.ActivityWebAuthBinding
import org.json.JSONObject


class WebAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebAuthBinding
    private lateinit var webView: WebView
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView
        session = SessionManager(applicationContext)

        webView.loadUrl("http://10.0.2.2:9090/oauth2/authorization/github")
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = WebViewClient()

        getAccessToken()

    }

    private fun getAccessToken() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null && url.contains("/users/token")) {
                    Log.i("URL", url)
                    webView.evaluateJavascript("(function() { return document.querySelector('pre').innerText; })();") { body ->
                        Log.i("BODY", body)
                        try {
                            val stringBody = body.replace("\\", "")
                             val jsonObject = JSONObject(stringBody.substring(stringBody.indexOf("{"), stringBody.lastIndexOf("}") + 1));
                            session.accessToken = "Bearer ${jsonObject.getString("access_token")}"
                            Log.d("TOKEN", session.accessToken.toString())
                            Log.d("JSON", jsonObject.toString())
                            startActivity(Intent(this@WebAuthActivity, ChatRoomActivity::class.java))
                            finish()
                        } catch (e: Exception) {
                            Log.e("JSON", "Erro ao converter o corpo em JSON: ${e.message} cause ${e.cause} message ${e.localizedMessage}")
                        }
                    }
                }
            }
        }
    }
}
