package com.waly.chat.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.waly.chat.R
import com.waly.chat.databinding.ActivityNewUserBinding
import com.waly.chat.models.CreateUser
import com.waly.chat.models.User
import com.waly.chat.utils.Environments
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewUserBinding
    private var count = 0
    private lateinit var user: CreateUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()

        user = CreateUser()
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
    private fun setupListeners() {
        binding.GithubSignIn.setOnClickListener {
            val intent = Intent(this, WebAuthActivity::class.java)
            intent.putExtra("param", Environments.GITHUB_PARAM)
            startActivity(intent)
            finish()
        }

        binding.GoogleSignIn.setOnClickListener {
            val intent = Intent(this, WebAuthActivity::class.java)
            intent.putExtra("param", Environments.GOOGLE_PARAM)
            startActivity(intent)
            finish()
        }

        binding.txtLogIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setLoading() {
        val blinkAnimation = AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_fade_in)
        val loginLoading = binding.loginLoading
        loginLoading.startAnimation(blinkAnimation)
        loginLoading.removeAllViews()
        loginLoading.addView(layoutInflater.inflate(R.layout.login_loading, null))
    }

    private fun setButton() {
        val input1 = binding.input1
        val input2 = binding.input2
        val signInButton = layoutInflater.inflate(R.layout.signin_button, null)
        val btn = signInButton.findViewById<Button>(R.id.btnSignIn)
        val login = binding.loginLoading

        login.removeAllViews()
        login.addView(signInButton)

        when (count) {
            0 -> {
                btn.text = "Próximo"
                btn.setOnClickListener {
                    if (input1.text.isEmpty() || input2.text.isEmpty() ||
                        input1.text.length < 4 || input2.text.length < 4 ||
                        input1.text.isBlank() || input2.text.isBlank()
                    ) {
                        showError()
                        return@setOnClickListener
                    }
                    user.name = input1.text.toString()
                    user.nickname = input2.text.toString()
                    count++
                    setButton()
                }
            }
            1 -> {
                input1.text.clear()
                input2.text.clear()
                input1.requestFocus()
                input1.hint = "Email"
                input1.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                input2.hint = "Senha"
                input2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                btn.text = "Sign In"
                btn.setOnClickListener {
                    if (input1.text.isEmpty() || input2.text.isEmpty() ||
                        input1.text.isBlank() || input2.text.isBlank() ||
                        !isEmail(input1.text.toString()) || input2.text.length < 6
                    ) {
                        showError()
                        return@setOnClickListener
                    }
                    user.email = input1.text.toString()
                    user.password = input2.text.toString()
                    setLoading()
                    createUser(user)
                }
            }
        }
    }

    private fun isEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return emailRegex.matches(email)
    }


    private fun showError() {
        val loginInput = binding.input1
        val passInput = binding.input2
        loginInput.setBackgroundResource(R.drawable.input_error_shape)
        passInput.setBackgroundResource(R.drawable.input_error_shape)

        Handler(Looper.getMainLooper()).postDelayed({
            loginInput.setBackgroundResource(R.drawable.input_message_shape)
            passInput.setBackgroundResource(R.drawable.input_message_shape)
        }, 3000)
    }

    private fun createUser(user: CreateUser) {
        val service = NetworkUtils.createServiceUser()
        service.createUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val newUser = response.body()
                    val intent = Intent(this@NewUserActivity, LoginActivity::class.java).apply {
                        putExtra("email", newUser?.email)
                        putExtra("password", user.password)
                    }
                    startActivity(intent)
                    finish()
                    return;
                }
                showError()
                count = 0
                setButton()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                showError()
                count = 0
                setButton()
                Log.e("PROFILE ACTIVITY", "Failed to fetch user data", t)
            }
        })
    }
}
