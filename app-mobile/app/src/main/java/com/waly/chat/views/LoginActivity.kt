package com.waly.chat.views

import LoginResponse
import NetworkUtils
import SessionManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.waly.chat.MainActivity
import com.waly.chat.R
import com.waly.chat.databinding.ActivityLoginBinding
import com.waly.chat.services.SaveUserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import android.graphics.Color
import android.view.View
import android.view.animation.Animation
import com.waly.chat.utils.Environments


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager
    private val CLIENT_ID = Environments.CLIENT_ID
    private val CLIENT_SECRET = Environments.CLIENT_SECRET
    private val GRANT_TYPE = "password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(this, NewUserActivity::class.java))
            finish()
        }

        binding.GithubLogin.setOnClickListener {
            val intent = Intent(this, WebAuthActivity::class.java)
            intent.putExtra("param", Environments.GITHUB_PARAM)
            startActivity(intent)
        }

        binding.GoogleLogin.setOnClickListener {
            val intent = Intent(this, WebAuthActivity::class.java)
            intent.putExtra("param", Environments.GOOGLE_PARAM)
            startActivity(intent)
        }

        session = SessionManager(applicationContext)

        if(intent.getStringExtra("email") != null && intent.getStringExtra("password") != null ){
            binding.edtEmail.setText(intent.getStringExtra("email"))
            binding.edtPassword.setText(intent.getStringExtra("password"))
        }

        setButton()

        backgroundAnimation()

    }


    private fun backgroundAnimation() {


        val backgroundView = findViewById<View>(R.id.backgroundView)

        // Animação de clareamento
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Animação de escurecimento
        val fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // Listener para repetir as animações
        val animationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                // Inverte as animações
                if (animation === fadeInAnimation) {
                    backgroundView.startAnimation(fadeOutAnimation)
                } else {
                    backgroundView.startAnimation(fadeInAnimation)
                }
            }
            override fun onAnimationRepeat(animation: Animation) {}
        }

        // Define os listeners para as animações
        fadeInAnimation.setAnimationListener(animationListener)
        fadeOutAnimation.setAnimationListener(animationListener)

        // Inicia a animação de clareamento
        backgroundView.startAnimation(fadeInAnimation)
    }

    fun setLoading() {
        val blinkAnimation = AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_in)
        val loginLoading = binding.loginLoading
        loginLoading.startAnimation(blinkAnimation)
        loginLoading.removeAllViews()
        loginLoading.addView(layoutInflater.inflate(R.layout.login_loading, null))
    }

    fun setButton() {
        val loginButton = layoutInflater.inflate(R.layout.login_button, null)
        val login = binding.loginLoading
        login.removeAllViews()
        login.addView(loginButton)
        loginButton.setOnClickListener {
            setLoading()
            login()
        }
    }

    fun login() {
        val service = NetworkUtils.createServiceLogin()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val combineCredentials = "$CLIENT_ID:$CLIENT_SECRET"
        val authorization = "Basic " + Base64.encodeToString(combineCredentials.toByteArray(), Base64.NO_WRAP)
        service.login(authorization, email, password, GRANT_TYPE)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        saveToken(loginResponse?.accessToken.toString(), loginResponse!!.expiresIn)
                        SaveUserData(applicationContext).saveLogedUser()
                        //startActivity(Intent(this@LoginActivity, ChatRoomActivity::class.java))
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                        return
                    }
                    showError()
                    setButton()
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showErrorTimeout()
                    setButton()
                    Log.i("LOGIN", t.message.toString())
                }
            })

    }

    fun showErrorTimeout() {
        val message = binding.txtErr
        message.text = "Error"
        message.setTextColor(Color.RED)
        val loginButton = layoutInflater.inflate(R.layout.login_button, null)
        val login = binding.loginLoading
        login.removeAllViews()
        login.addView(loginButton)
        loginButton.setOnClickListener {
            login()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            message.text = "Login"
            message.setTextColor(Color.WHITE)
        }, 3000)
    }
    fun showError() {
        val loginInput = binding.edtEmail
        val passInput = binding.edtPassword
        loginInput.setBackgroundResource(R.drawable.input_error_shape)
        passInput.setBackgroundResource(R.drawable.input_error_shape)

        Handler(Looper.getMainLooper()).postDelayed({
            loginInput.setBackgroundResource(R.drawable.input_message_shape)
            passInput.setBackgroundResource(R.drawable.input_message_shape)
        }, 3000)
    }

    fun saveToken(token: String, expiresIn: Long) {
        session.accessToken = "Bearer $token"
        session.accessTokenExpiration = Date().time.plus(expiresIn * 1000).toString()
    }
}