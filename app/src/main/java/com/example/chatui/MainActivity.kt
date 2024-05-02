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
import androidx.constraintlayout.motion.widget.MotionLayout
import com.bumptech.glide.Glide
import com.example.chatui.configs.WebSocketConfig
import com.example.chatui.databinding.ActivityMainBinding
import com.example.chatui.databinding.MotionLayoutMainBinding
import com.example.chatui.models.FullTeam
import com.example.chatui.models.Team
import com.example.chatui.models.TeamFullResponse
import com.example.chatui.models.TeamMin
import com.example.chatui.views.ChatActivity
import com.example.chatui.views.ChatRoomActivity
import com.example.chatui.views.LoginActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.subscribeText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var session: SessionManager
    private lateinit var motionLayout: MotionLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(applicationContext)
        motionLayout = findViewById(R.id.motion_base_item)


        binding.userName.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        val buttonHome = motionLayout.findViewById<Button>(R.id.button_home)
        val buttonProfile = motionLayout.findViewById<Button>(R.id.button_profile)
        val buttonChat = motionLayout.findViewById<Button>(R.id.button_chat)
        val buttonSearch = motionLayout.findViewById<Button>(R.id.button_search)


        buttonProfile.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java))
        }
        buttonChat.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java))
        }
        buttonSearch.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java).putExtra("search", "true"))
        }

        fetchTeamData()
        websocketConnect()
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
        val list = motionLayout.findViewById<LinearLayout>(R.id.mainTeams)

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
        teamsLayout.addView(motionLayout)
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

    fun websocketConnect() {
        val nickname = session.userLogged
        val webSocketConfig = WebSocketConfig(applicationContext)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val session: StompSession = withContext(Dispatchers.IO) {
                    webSocketConfig.connect()
                }
                val subs: Flow<String> = session.subscribeText("/user/${nickname}/queue/chats")

                var teamList: HashMap<Int, Team> = HashMap()
                subs.collectLatest {
                    Log.i("STOMP", "Received message: $it")
                    teamList = jsonStringToTeamList(it, teamList)
                    if(teamList.size > 1)
                        teamList = removeDuplicates(teamList, teamList[teamList.keys.max()]!!.id, teamList.keys.max()!!)
                    showChatRooms(teamList)
                }


                Log.i("STOMP", "Connected to STOMP")


            } catch (e: Exception) {
                Log.e("STOMP", "Error: " + e.message + e.stackTraceToString())
            }
        }
    }
    fun jsonStringToTeamList(jsonString: String, teamList: HashMap<Int, Team>): HashMap<Int, Team> {
        val gson = Gson()
        val listType = object : TypeToken<List<Team>>() {}.type
        var teams: List<Team> =  gson.fromJson(jsonString, listType)
        teams.forEach { team ->
            var i = teamList.keys.maxOrNull()
            if(i == null) i = 0
            else i++
            Log.i("TEAM", "MAX: ${i}")
            teamList[i] = team
        }
        return teamList
    }

    fun removeDuplicates(teams: HashMap<Int, Team>, teamId: String, key: Int): HashMap<Int, Team> {
        var keyToRemove = -1
        teams.forEach {
            if(it.value.id == teamId && it.key != key) {
                keyToRemove = it.key
            }
        }
        if(keyToRemove != -1)
            teams.remove(keyToRemove)
        return teams
    }
    fun showChatRooms(teams: HashMap<Int, Team>) {
        val scrollContainer = binding.motionBaseMain
        val scrollView = motionLayout.findViewById<LinearLayout>(R.id.ScrollChatMain)

        Log.i("SHOW CHAT ROOM", "Showing chat rooms ${teams.keys.maxOrNull()}")

        var j = teams.keys.maxOrNull() ?: return
        for (i in j downTo  0 step 1) {
            if(teams[i] != null) {
                val roomCard = layoutInflater.inflate(R.layout.card_chat_item , scrollView , false)
                val cardImage = layoutInflater.inflate(R.layout.home_image, scrollContainer, false)

                val teamName = roomCard.findViewById<TextView>(R.id.text_group_name)
                teamName.text = teams[i]!!.roomName

                val latestMsg = roomCard.findViewById<TextView>(R.id.text_latest_message)
                latestMsg.text = teams[i]!!.latestMessage

                val image = cardImage.findViewById<ShapeableImageView>(R.id.statusImage)

                Glide
                    .with(applicationContext)
                    .load(teams[i]!!.imgUrl)
                    .centerCrop()
                    .into(image)

                roomCard.findViewById<LinearLayout>(R.id.cardImage).addView(cardImage)

                val cardChat = roomCard.findViewById<LinearLayout>(R.id.card_chat_item)

                cardChat.setOnClickListener {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("teamId", teams[i]!!.id)

                    startActivity(intent)
                }
                image.setOnClickListener {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("teamId", teams[i]!!.id)

                    startActivity(intent)
                }

                scrollView.addView(roomCard)
                if(i==5) break;
            }
        }
        scrollContainer.removeAllViews()
        scrollContainer.addView(motionLayout)
    }


}