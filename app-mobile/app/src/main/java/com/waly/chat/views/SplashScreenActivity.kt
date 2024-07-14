package com.waly.chat.views

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.waly.chat.MainActivity
import com.waly.chat.databinding.ActivitySplashScreenBinding
import com.waly.chat.notification.MessageNotification
import io.sentry.Sentry
import java.util.Date


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        Sentry.init { options ->
            options.dsn = "https://ea45d9d797f0251b7b7efc7b12a41761@sentry.api.waly.dev.br/1"
        }

        MessageNotification(this).createNotificationChannel()

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