package com.example.chatui.views

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatui.R
import com.example.chatui.databinding.ActivitySplashScreenBinding
import com.example.chatui.notification.MessageNotification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(verifyTokenExpiration()) {
            Intent(this, ChatRoomActivity::class.java).also {
                startActivity(it)
                finish()
            }
            return;
        }
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            finish()
        }

    }

    fun verifyTokenExpiration() : Boolean {
        val session = SessionManager(applicationContext)
        val accessToken = session.accessToken
        val tokenExpiration = session.accessTokenExpiration?.toLongOrNull()

        Log.i("TOKEN", "TOKEN: $accessToken")
        Log.i("TOKEN", "TOKEN EXPIRATION: $tokenExpiration")

        if (accessToken == null || tokenExpiration == null) return false

        val expirationDate = Date(tokenExpiration)

        val currentDate = Date()

        if (currentDate.after(expirationDate)) {
            session.clearSession()
            return false
        }
        return true
    }
}