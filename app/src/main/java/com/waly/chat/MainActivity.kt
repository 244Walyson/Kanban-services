package com.waly.chat

import NetworkUtils
import SessionManager
import android.content.ContentValues
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.waly.chat.configs.WebSocketConfig
import com.waly.chat.databinding.ActivityMainBinding
import com.waly.chat.models.FcmToken
import com.waly.chat.models.Team
import com.waly.chat.models.TeamFullResponse
import com.waly.chat.models.TeamMin
import com.waly.chat.models.User
import com.waly.chat.views.ChatActivity
import com.waly.chat.views.ChatRoomActivity
import com.waly.chat.views.NotificationActivity
import com.waly.chat.views.ProfileActivity
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
    private lateinit var layoutContainer: LinearLayout
    private lateinit var allChat: MutableList<Team>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allChat = mutableListOf()

        session = SessionManager(applicationContext)
        motionLayout = findViewById(R.id.motion_layout_main_id)
        layoutContainer = findViewById(R.id.main_layout)

        motionLayout.findViewById<ImageView>(R.id.notificationIcon).setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        val buttonNotification = motionLayout.findViewById<Button>(R.id.button_home)
        val buttonProfile = motionLayout.findViewById<Button>(R.id.button_profile)
        val buttonChat = motionLayout.findViewById<Button>(R.id.button_chat)
        val buttonSearch = motionLayout.findViewById<Button>(R.id.button_search)


        buttonProfile.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
            finish()
        }
        buttonChat.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java))
            finish()
        }
        buttonSearch.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java).putExtra("search", "true"))
            finish()
        }
        buttonNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
            finish()
        }

        fetchTeamData()
        websocketConnect()
        showHeader()

        Log.i("FCM", "Fetching FCM registration token")
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            Log.i("FCM", "Fetching FCM registration token AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            saveFcmToken(token!!)
            Log.e("myToken", "" + token)
        })
    }

    private fun showHeader() {
        val service = NetworkUtils.createServiceUser()
        val token = session.accessToken!!
        service.getUser(token)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        session.userLoggedName = user?.username
                        session.userLoggedImg = user?.username
                        val txtName = motionLayout.findViewById<TextView>(R.id.userName)
                        txtName.text = user?.username
                        val imgLayout = motionLayout.findViewById<LinearLayout>(R.id.userImageLayout)
                        val imgInf = layoutInflater.inflate(R.layout.home_image, imgLayout, false)
                        val img = imgInf.findViewById<ImageView>(R.id.statusImage)
                        img.setOnClickListener {
                            startActivity(Intent(applicationContext, ProfileActivity::class.java))
                        }

                        Glide
                            .with(applicationContext)
                            .load(user?.imgUrl)
                            .centerCrop()
                            .into(img)
                        imgLayout.removeAllViews()
                        imgLayout.addView(imgInf)
                        layoutContainer.removeAllViews()
                        layoutContainer.addView(motionLayout)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("USER", "Failed to fetch user")
                    Log.e("USER", t.message!!)
                    Log.e("USER", t.cause.toString())
                    t.printStackTrace()
                }
            })
    }

    private fun saveFcmToken(token: String) {
        val session = SessionManager(applicationContext)

        if (!session.tokenSaved) {
            val tokenToSave: FcmToken = FcmToken(token)
            val service = NetworkUtils.createServiceSaveToken()

            Log.i("SAVING TOKEN", "Saving token $token")

            service.saveToken(tokenToSave, session.accessToken!!)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            session.tokenSaved = true
                            Log.i("PUSHH PUSHH ROOM", "Token saved")
                            return
                        }
                        Log.e(
                            "PUSHH PUSHH ROOM",
                            "Token not saved ${response.code()} ${response.message()}"
                        )
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.i("PUSHH PUSHH ROOM eRROR", t.message.toString())
                    }
                })
        }
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
        val list = motionLayout.findViewById<LinearLayout>(R.id.mainTeams)

        teams?.forEach { team ->
            val gradient = getGradientDrawable()
            Log.i("TEAM", "TEAM ${team.roomName}")
            val groupItem = layoutInflater.inflate(R.layout.group_item, motionLayout, false)
            val gradientImage = groupItem.findViewById<ImageView>(R.id.gradientImage)
            gradientImage.background = gradient
            val teamName = groupItem.findViewById<TextView>(R.id.mainTeamName)
            teamName.text = team.roomName
            val teamDesc = groupItem.findViewById<TextView>(R.id.mainTeamDesc)
            teamDesc.text = team.description
            list.addView(groupItem)
        }
        layoutContainer.removeAllViews()
        layoutContainer.addView(motionLayout)
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

                subs.collectLatest {
                    Log.i("STOMP", "Received message: $it")
                     allChat.addAll(jsonStringToTeamList(it))
                    removeDuplicates(allChat.last().id)
                    showChatRooms(allChat)
                }


                Log.i("STOMP", "Connected to STOMP")


            } catch (e: Exception) {
                Log.e("STOMP", "Error: " + e.message + e.stackTraceToString())
            }
        }
    }

    fun jsonStringToTeamList(jsonString: String): MutableList<Team> {
        val gson = Gson()
        val listType = object : TypeToken<List<Team>>() {}.type
        var teams: List<Team> = gson.fromJson(jsonString, listType)
        return teams.toMutableList()
    }

    fun removeDuplicates(chatId: String) {
        val chatToRemove = allChat.filter { it.id == chatId }

        if (chatToRemove.size > 1) {
            val lastChat = chatToRemove.last()
            allChat.remove(lastChat)
        }
    }

    fun showChatRooms(teams: MutableList<Team>) {
        val scrollView = motionLayout.findViewById<LinearLayout>(R.id.ScrollChatMain)

        Log.i("SHOW CHAT ROOM", "Showing chat rooms ")

        var x = 0
        teams.forEach { team ->
            if (x >= 3) return@forEach;
            if (team.id.contains("U")) {
                x++
                val roomCard = layoutInflater.inflate(R.layout.card_chat_item, scrollView, false)
                val cardImage = layoutInflater.inflate(R.layout.home_image, scrollView, false)

                val teamName = roomCard.findViewById<TextView>(R.id.text_group_name)
                teamName.text = team.roomName

                val latestMsg = roomCard.findViewById<TextView>(R.id.text_latest_message)
                latestMsg.text = team.latestMessage

                val image = cardImage.findViewById<ShapeableImageView>(R.id.statusImage)

                Glide
                    .with(applicationContext)
                    .load(team.imgUrl)
                    .centerCrop()
                    .into(image)

                roomCard.findViewById<LinearLayout>(R.id.cardImage).addView(cardImage)

                val cardChat = roomCard.findViewById<LinearLayout>(R.id.card_chat_item)

                cardChat.setOnClickListener {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("teamId", team.id)

                    startActivity(intent)
                }
                image.setOnClickListener {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("teamId", team.id)

                    startActivity(intent)
                }
                scrollView.addView(roomCard)
            }
        }
        layoutContainer.removeAllViews()
        layoutContainer.addView(motionLayout)
    }


}