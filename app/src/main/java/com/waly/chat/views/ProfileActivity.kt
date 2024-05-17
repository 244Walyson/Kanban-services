package com.waly.chat.views

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.waly.chat.R
import com.waly.chat.databinding.ActivityProfileBinding
import com.waly.chat.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.logoutButton.setOnClickListener {
            session.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fetchUserData()
    }

    private fun fetchUserData() {
        val token = session.accessToken
        val service = NetworkUtils.createServiceUser()

        service.getUser(token!!)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            showUserDetails(user)
                        }
                    }
                    Log.i("PROFILE FRAG", "User data fetched")
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("PROFILE FRAG", "Error fetching user data", t)
                }
            })
    }

    private fun showUserDetails(user: User) {

        val userName = binding.userName
        userName.text = user.username

        val nickname = binding.userNick
        nickname.text = user.nickname

        val userInfo = binding.userInfo
        val userImage = layoutInflater.inflate(R.layout.profile_image, userInfo, false)
        val imageView = userImage.findViewById<ImageView>(R.id.userImage)

        Glide
            .with(this)
            .load(user.imgUrl)
            .centerCrop()
            .into(imageView)

        userInfo.removeAllViews()
        userInfo.addView(userImage)

        Log.i("PROFILE FRAG", "User details: ${user.username}")
    }
}