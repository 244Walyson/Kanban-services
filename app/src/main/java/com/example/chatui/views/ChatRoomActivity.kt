package com.example.chatui.views

import SessionManager
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chatui.R
import com.example.chatui.configs.WebSocketConfig
import com.example.chatui.databinding.ActivityChatRoomBinding
import com.example.chatui.models.FcmToken
import com.example.chatui.models.Team
import com.example.chatui.notification.MessageNotification
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.subscribeText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatRoomBinding.inflate(layoutInflater)

        setContentView(binding.root)

        websocketConnect()

        MessageNotification(this).createNotificationChannel()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            saveFcmToken(token!!)
            Log.e("myToken", "" + token)
        })

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun saveFcmToken(token: String) {
        val session = SessionManager(applicationContext)

        if(!session.tokenSaved){
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
                        Log.e("PUSHH PUSHH ROOM", "Token not saved ${response.code()} ${response.message()}")
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.i("PUSHH PUSHH ROOM eRROR", t.message.toString())
                    }
                })
        }
    }


    fun websocketConnect() {
        val webSocketConfig = WebSocketConfig(applicationContext)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val session: StompSession = withContext(Dispatchers.IO) {
                    webSocketConfig.connect()
                }
                val subs: Flow<String> = session.subscribeText("/app/chats")


                val teamList: List<Team> = jsonStringToTeamList(subs.first());


                Log.i("STOMP", "Connected to STOMP")

                showChatRooms(teamList)

            } catch (e: Exception) {
                Log.e("STOMP", "Error: " + e.message + e.stackTraceToString())
            }
        }
    }
    fun jsonStringToTeamList(jsonString: String): List<Team> {
        val gson = Gson()
        val listType = object : TypeToken<List<Team>>() {}.type
        return gson.fromJson(jsonString, listType)
    }

    fun showChatRooms(teams: List<Team>) {
        val scrollView = binding.ScrollChats

        teams?.forEach {team ->
            val roomCard = layoutInflater.inflate(R.layout.card_chat_item , scrollView , false)

            val teamName = roomCard.findViewById<TextView>(R.id.text_group_name)
            teamName.text = team.roomName

            val latestMsg = roomCard.findViewById<TextView>(R.id.text_latest_message)
            latestMsg.text = team.latestMessage

            val image = roomCard.findViewById<ImageView>(R.id.image_group)

            Glide
                .with(applicationContext)
                .load(team.imgUrl)
                .centerCrop()
                .into(image)

            val cardChat = roomCard.findViewById<LinearLayout>(R.id.card_chat_item)

            cardChat.setOnClickListener {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("teamId", team.id)
                startActivity(intent)
            }

            scrollView.addView(roomCard)

        }
    }


}