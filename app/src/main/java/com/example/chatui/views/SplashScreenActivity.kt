package com.example.chatui.views

import SessionManager
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.chatui.MainActivity
import com.example.chatui.databinding.ActivitySplashScreenBinding
import com.example.chatui.viewModels.MainViewModel
import java.util.Date


class SplashScreenActivity : AppCompatActivity() {


    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        redirectBasedOnTokenValidity()
    }

    private fun redirectBasedOnTokenValidity() {
        if (verifyTokenExpiration()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
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