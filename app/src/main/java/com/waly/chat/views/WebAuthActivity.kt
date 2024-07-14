package com.waly.chat.views

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.waly.chat.MainActivity
import com.waly.chat.databinding.ActivityWebAuthBinding
import com.waly.chat.services.SaveUserData
import com.waly.chat.utils.Environments
import org.json.JSONObject
import java.util.Date


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

        val url = getUrl()
        webView.loadUrl(url)
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = WebViewClient()

        getAccessToken()

    }

    private fun getUrl(): String {
        val param = intent.getStringExtra("param")
        return "${Environments.BASE_URL}$param"
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
                            saveToken(jsonObject.getString("access_token"), jsonObject.getLong("expires_in"))
                            SaveUserData(applicationContext).saveLogedUser()
                            intent = Intent(this@WebAuthActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Log.e("JSON", "Erro ao converter o corpo em JSON: ${e.message} cause ${e.cause} message ${e.localizedMessage}")
                        }
                    }
                }
            }
        }
    }
    fun saveToken(token: String, expiresIn: Long) {
        session.accessToken = "Bearer $token"
        session.accessTokenExpiration = Date().time.plus(expiresIn * 1000).toString()
    }
}
