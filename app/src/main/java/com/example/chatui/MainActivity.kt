package com.example.chatui

import NetworkUtils
import SessionManager
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.chatui.databinding.ActivityMainBinding
import com.example.chatui.models.FullTeam
import com.example.chatui.models.TeamFullResponse
import com.example.chatui.models.TeamMin
import com.example.chatui.views.ChatRoomActivity
import com.example.chatui.views.LoginActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(applicationContext)

        binding.userName.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }


        fetchTeamData()
    }

    private fun showStatus() {}

    private fun fetchTeamData() {
        val token = session.accessToken
        val service = NetworkUtils.createServiceTeamFull()
        service.getTeams(token!!)
            .enqueue(object : Callback<TeamFullResponse> {
                override fun onResponse(
                    call: Call<TeamFullResponse>,
                    response: Response<TeamFullResponse>
                ) {
                    Log.i("Teams", "${response.code()} ${response.body()} ${response.message()}")
                    if (response.isSuccessful) {
                        Log.i("Teams", response.body().toString())
                        val teams = response.body()?.content
                        showTeams(teams)
                    }
                }

                override fun onFailure(call: Call<TeamFullResponse>, t: Throwable) {
                    Log.e("Teams", "Failed to fetch teams")
                    Log.e("Teams", t.message!!)
                    Log.e("Teams", t.cause.toString())
                    t.printStackTrace()
                }
            })
    }

    private fun showTeams(teams: List<TeamMin>?) {
        Log.i("Teams", "Show Teams")
        val teamsLayout = binding.motionBaseMain
        val motionInflate = layoutInflater.inflate(R.layout.motion_layout_main, teamsLayout, false)
        val list = motionInflate.findViewById<LinearLayout>(R.id.mainTeams)

        val buttonHome = motionInflate.findViewById<Button>(R.id.button_home)
        val buttonProfile = motionInflate.findViewById<Button>(R.id.button_profile)
        val buttonChat = motionInflate.findViewById<Button>(R.id.button_chat)
        val buttonSearch = motionInflate.findViewById<Button>(R.id.button_search)

        buttonHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        buttonProfile.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java))
        }
        buttonChat.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java))
        }
        buttonSearch.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java))
        }

        teams?.forEach {team ->
            val gradient = getGradientDrawable()
            Log.i("TEAM", "TEAM ${team.roomName}")
            val groupItem = layoutInflater.inflate(R.layout.group_item, teamsLayout, false)
            val gradientImage = groupItem.findViewById<ImageView>(R.id.gradientImage)
            gradientImage.background = gradient
            val teamName = groupItem.findViewById<TextView>(R.id.mainTeamName)
            teamName.text = team.roomName
            val teamDesc = groupItem.findViewById<TextView>(R.id.mainTeamDesc)
            teamDesc.text = team.description
            list.addView(groupItem)
        }
        teamsLayout.removeAllViews()
        teamsLayout.addView(motionInflate)
    }

    private fun getGradientDrawable(): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        val startColor = getRandomColor()
        val endColor = getRandomColor()
        gradientDrawable.colors = intArrayOf(startColor, endColor)
        gradientDrawable.orientation = GradientDrawable.Orientation.BL_TR
        return gradientDrawable
    }

    private fun showTasks() {}

    private fun getRandomColor(): Int {
        val r = (0..255).random()
        val g = (0..255).random()
        val b = (0..255).random()
        return 0xff000000.toInt() or (r shl 16) or (g shl 8) or b
    }

}