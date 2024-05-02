package com.example.chatui.views

import LoginResponse
import NetworkUtils
import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chatui.MainActivity
import com.example.chatui.databinding.ActivityLoginBinding
import com.example.chatui.models.User
import com.example.chatui.services.SaveUserData
import com.example.chatui.utils.Environments
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date


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

        binding.btnLogin.setOnClickListener {
            login()
        }

    }


    fun login() {
        val service = NetworkUtils.createServiceLogin()

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        val combineCredentials = "$CLIENT_ID:$CLIENT_SECRET"
        val authorization =
            "Basic " + Base64.encodeToString(combineCredentials.toByteArray(), Base64.NO_WRAP)

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

                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.i("LOGIN", t.message.toString())
                }
            })

    }

    fun saveToken(token: String, expiresIn: Long) {
        session.accessToken = "Bearer $token"
        session.accessTokenExpiration = Date().time.plus(expiresIn * 1000).toString()
    }
}