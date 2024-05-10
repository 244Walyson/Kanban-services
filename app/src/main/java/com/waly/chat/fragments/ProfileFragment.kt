package com.waly.chat.fragments

import SessionManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.waly.chat.R
import com.waly.chat.databinding.FragmentProfileBinding
import com.waly.chat.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback {
            returnToActivity()
        }

        binding = FragmentProfileBinding.inflate(layoutInflater)
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
    private fun returnToActivity() {
        requireActivity().findViewById<FrameLayout>(R.id.chatFrameLayout).visibility = View.GONE
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.remove(this@ProfileFragment)
        fragmentTransaction.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        session = SessionManager(requireContext())
        fetchUserData()

        val backButton = binding.backButton
        backButton.setOnClickListener {
            backButton.animation = android.view.animation.AnimationUtils.loadAnimation(requireContext(), androidx.appcompat.R.anim.abc_slide_in_top)
            returnToActivity()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {

            }
    }
}