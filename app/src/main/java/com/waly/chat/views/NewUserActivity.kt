package com.waly.chat.views

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.waly.chat.R
import com.waly.chat.databinding.ActivityNewUserBinding
import com.waly.chat.utils.Environments

class NewUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.GithubSignIn.setOnClickListener {
            val intent = Intent(this, WebAuthActivity::class.java)
            intent.putExtra("param", Environments.GITHUB_PARAM)
            startActivity(intent)
        }

        binding.GoogleSignIn.setOnClickListener {
            val intent = Intent(this, WebAuthActivity::class.java)
            intent.putExtra("param", Environments.GOOGLE_PARAM)
            startActivity(intent)
        }

        binding.txtLogIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        setButton()
    }

    fun setLoading() {
        val blinkAnimation = AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_in)
        val loginLoading = binding.loginLoading
        loginLoading.startAnimation(blinkAnimation)
        loginLoading.removeAllViews()
        loginLoading.addView(layoutInflater.inflate(R.layout.login_loading, null))
    }

    fun setButton() {
        val signInButton = layoutInflater.inflate(R.layout.signin_button, null)
        val login = binding.loginLoading
        login.removeAllViews()
        login.addView(signInButton)
        signInButton.setOnClickListener {
            setLoading()
        }
    }

}