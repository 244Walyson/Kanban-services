package com.example.chatui.views

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chatui.MainActivity
import com.example.chatui.databinding.ActivitySplashScreenBinding
import java.util.Date

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(verifyTokenExpiration()) {
            Intent(this, MainActivity::class.java).also {
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