package com.waly.chat

import NetworkUtils
import SessionManager
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
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
import com.waly.chat.notification.MessageNotification
import com.waly.chat.views.ChatActivity
import com.waly.chat.views.ChatRoomActivity
import com.waly.chat.views.NotificationActivity
import com.waly.chat.views.ProfileActivity
import com.waly.chat.views.TeamActivity
import com.waly.chat.views.WebViewGithubActivity
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import android.Manifest
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.waly.chat.views.StatusActivity


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
            //finish()
        }
        buttonChat.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java))
            //finish()
        }
        buttonSearch.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java).putExtra("search", "true"))
            //finish()
        }
        buttonNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
            //finish()
        }


        fetchTeamData()
        websocketConnect()
        showHeader()

        val swipeRefreshLayout: SwipeRefreshLayout = motionLayout.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchTeamData()
            showHeader()
            if(allChat.isEmpty()) websocketConnect()
            swipeRefreshLayout.isRefreshing = false
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            saveFcmToken(token!!)
        })



        motionLayout.findViewById<View>(R.id.firstStatus).setOnClickListener {
            startActivity(Intent(this, StatusActivity::class.java))
        }

        askNotificationPermission()
    }



    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
//                Log.e(TAG, "PERMISSION_GRANTED")
                // FCM SDK (and your app) can post notifications.
            } else {
//                Log.e(TAG, "NO_PERMISSION")
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {}
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
                            .placeholder(R.drawable.unknow_image)
                            .centerCrop()
                            .into(img)
                        imgLayout.removeAllViews()
                        imgLayout.addView(imgInf)
                        layoutContainer.removeAllViews()
                        layoutContainer.addView(motionLayout)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun saveFcmToken(fcmToken: String) {
        val session = SessionManager(applicationContext)

        if (!session.tokenSaved) {
            val tokenToSave: FcmToken = FcmToken(fcmToken)
            val service = NetworkUtils.createServiceNotification()

            service.saveFcmToken(session.accessToken!!, tokenToSave)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            session.tokenSaved = true
                            return
                        }
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
                    t.printStackTrace()
                }
            })
    }

    private fun showTeams(teams: List<TeamMin>?) {
        val list = motionLayout.findViewById<LinearLayout>(R.id.mainTeams)
        list.removeAllViews()
        teams?.forEach { team ->
            val gradient = getGradientDrawable()
            val groupItem = layoutInflater.inflate(R.layout.group_item, motionLayout, false)
            val gradientImage = groupItem.findViewById<ImageView>(R.id.gradientImage)
            gradientImage.background = gradient
            val teamName = groupItem.findViewById<TextView>(R.id.mainTeamName)
            teamName.text = team.roomName
            val teamDesc = groupItem.findViewById<TextView>(R.id.mainTeamDesc)
            teamDesc.text = team.description
            val githubIcon = groupItem.findViewById<ImageView>(R.id.iconGithubHexagon)
            githubIcon.setOnClickListener {
                val intent = Intent(this, WebViewGithubActivity::class.java)
                intent.putExtra("url", team.githubLink)
                startActivity(intent)
            }

            gradientImage.setOnClickListener {
                val intent = Intent(this, TeamActivity::class.java)
                intent.putExtra("teamId", team.id)
                startActivity(intent)
            }


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
                     allChat.addAll(jsonStringToTeamList(it))
                    removeDuplicates(allChat.last().id)
                    showChatRooms(allChat)
                }
            } catch (e: Exception) {
                Log.e("STOMP", "Error: " + e.message + e.stackTraceToString())
            }
        }
    }

    fun removeDuplicates(chatId: String) {
        val chatToRemove = allChat.filter { it.id == chatId }

        if (chatToRemove.size > 1) {
            val lastChat = chatToRemove.last()
            allChat.remove(lastChat)
        }
    }


    fun jsonStringToTeamList(jsonString: String): MutableList<Team> {
        val gson = Gson()
        val listType = object : TypeToken<List<Team>>() {}.type
        var teams: List<Team> = gson.fromJson(jsonString, listType)
        return teams.toMutableList()
    }


    fun showChatRooms(teams: MutableList<Team>) {
        val scrollView = motionLayout.findViewById<LinearLayout>(R.id.ScrollChatMain)

        var x = 0
        teams.forEach { team ->
            if (x >= 3) return@forEach;
            if (team.id.contains("U")) {
                x++
                val roomCard = layoutInflater.inflate(R.layout.card_chat_item, scrollView, false)
                val cardImage = layoutInflater.inflate(R.layout.home_image, scrollView, false)

                val teamName = roomCard.findViewById<TextView>(R.id.text_group_name)
                teamName.text = team.roomName

                val latestActivity = roomCard.findViewById<TextView>(R.id.text_last_activity)
                var latestText = getTimeAgo(parseDateToMillis(team.lastActivity))
                if (latestText.contains("agora")) latestActivity.setTextColor(getColor(R.color.blue))
                else latestActivity.setTextColor(resources.getColor(R.color.gray_tertiary))
                latestActivity.text = latestText

                val unreadMessages = roomCard.findViewById<TextView>(R.id.text_unread_messages)
                unreadMessages.visibility = View.GONE

                val latestMsg = roomCard.findViewById<TextView>(R.id.text_latest_message)
                latestMsg.text = team.latestMessage

                val image = cardImage.findViewById<ShapeableImageView>(R.id.statusImage)

                Glide
                    .with(applicationContext)
                    .load(team.imgUrl)
                    .placeholder(R.drawable.unknow_image)
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

    fun parseDateToMillis(dateString: String): Long {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = OffsetDateTime.parse(dateString, formatter)
        return dateTime.toInstant().toEpochMilli()
    }

    fun getTimeAgo(lastActivityMillis: Long): String {
        val currentTimeMillis = System.currentTimeMillis()
        val diffMillis = currentTimeMillis - lastActivityMillis

        val lastActivityDateTime = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(lastActivityMillis),
            ZoneId.systemDefault()
        )
        val currentDate = LocalDate.now()
        val lastActivityDate = lastActivityDateTime.toLocalDate()

        val diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diffMillis)
        val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
        val diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis)
        val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

        return when {
            diffSeconds < 30 -> "agora"
            diffSeconds < 60 -> "há alguns segundos"
            diffMinutes < 2 -> "há 1 minuto"
            diffMinutes < 60 -> "há $diffMinutes minutos"
            diffHours < 24 && lastActivityDate.isEqual(currentDate) -> {
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                "Hoje às ${lastActivityDateTime.format(timeFormatter)}"
            }
            diffHours < 24 -> {
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                "Ontem às ${lastActivityDateTime.format(timeFormatter)}"
            }
            diffDays < 2 -> "$diffDays dia atrás"
            diffDays in 2..6 -> "$diffDays dias atrás"
            else -> {
                val diffWeeks = diffDays / 7
                "$diffWeeks semanas atrás"
            }
        }
    }

}