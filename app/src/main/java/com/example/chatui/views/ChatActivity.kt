package com.example.chatui.views

import SessionManager
import android.os.Bundle
import android.util.Log
import android.view.View.FOCUS_DOWN
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chatui.R
import com.example.chatui.configs.WebSocketConfig
import com.example.chatui.databinding.ActivityChatBinding
import com.example.chatui.models.Message
import com.example.chatui.models.MessageSent
import com.example.chatui.models.Team
import com.example.chatui.models.TeamFull
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val scrollView = binding.chatScrollView

        scrollView.post {
            scrollView.fullScroll(FOCUS_DOWN)
        }

        channelSubscribe()

        binding.btnSend.setOnClickListener {
            sendMessage()
        }
    }

    fun sendMessage() {
        val message = binding.edtMessage.text.toString()
        if(message.isEmpty()) return;
        binding.edtMessage.text.clear()

        val teamId = intent.getStringExtra("teamId")
        val websocketConfig = WebSocketConfig(applicationContext)

        val textMsg: MessageSent = MessageSent(message)

        Log.i("ChatActivity", "Message: ${textMsg.toString()}")

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val session: StompSession = withContext(Dispatchers.IO) {
                    websocketConfig.connect()
                }
                session.sendText("/app/chat/${teamId}", textMsg.toString())
            } catch (e: Exception) {
                Log.e("ChatActivity", "Error: $e")
            }
        }
    }

    fun channelSubscribe() {
        val websocketConfig = WebSocketConfig(applicationContext)
        val session = SessionManager(applicationContext)
        val myNickname = session.userLogged

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val session: StompSession = withContext(Dispatchers.IO) {
                    websocketConfig.connect()
                }
                val teamId = intent.getStringExtra("teamId")
                val subs: Flow<String> = session.subscribeText("/user/${teamId}/queue/messages")

                Log.i("TEAM", "TEAM ID: $teamId")

                buildChat(subs)


            } catch (e: Exception) {
                Log.e("ChatActivity", "Error: $e")
            }
        }
    }

    fun buildChat(chatRoom: Flow<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            chatRoom.collectLatest { chat ->
                if (chat.contains("roomName")) {
                    val room = Gson().fromJson(chat, TeamFull::class.java)
                    showChatRoom(room)
                    return@collectLatest
                }
                val message = Gson().fromJson(chat, Message::class.java)
                showMessage(arrayListOf(message))
            }
        }
    }

    fun showChatRoom(team: TeamFull) {
        Log.i("TEAM", "TEAM: $team")
        val toolbar = binding.chatToolbar
        val headerChat = layoutInflater.inflate(R.layout.header_chat, toolbar, false)

        val roomTitle = headerChat.findViewById<TextView>(R.id.room_name)
        roomTitle.text = team.roomName

        val roomDesc = headerChat.findViewById<TextView>(R.id.room_description)
        roomDesc.text = team.description

        val roomImage = headerChat.findViewById<ImageView>(R.id.userImage)
        Glide
            .with(applicationContext)
            .load(team.imgUrl)
            .centerCrop()
            .into(roomImage)
        toolbar.addView(headerChat)
        showMessage(team.messages)
    }

    fun showMessage(message: List<Message>) {
        val myNickname = SessionManager(applicationContext).userLogged

        Log.i("MESSAGE", "MESSAGE: $message")
        message.forEach { msg ->
            if(msg.sender.nickName.equals(myNickname)){
                showMyMessage(msg)
                return@forEach
            }
            showOtherMessage(msg)
        }
    }

    fun showMyMessage(msg: Message) {
        Log.i("MY MESSAGE", "MESSAGE: $msg")
        val chatContainer = binding.chatContainer
        val myMessage = layoutInflater.inflate(R.layout.chat_item_my, chatContainer, false)

        val txtMsg = myMessage.findViewById<TextView>(R.id.chat_my)
        txtMsg.text = msg.content

        chatContainer.addView(myMessage)
    }
    fun showOtherMessage(msg: Message) {
        Log.i("OTHER MESSAGE", "MESSAGE: $msg")
        val chatContainer = binding.chatContainer
        val otherMessage = layoutInflater.inflate(R.layout.chat_item_other, chatContainer, false)

        val txtMsg = otherMessage.findViewById<TextView>(R.id.message_content)
        txtMsg.text = msg.content

        val senderName = otherMessage.findViewById<TextView>(R.id.sender_name)
        senderName.text = msg.sender.name

        val senderImage = otherMessage.findViewById<ImageView>(R.id.senderImage)
        Glide
            .with(applicationContext)
            .load(msg.sender.imgUrl)
            .centerCrop()
            .placeholder(R.drawable.color12)
            .into(senderImage)
        chatContainer.addView(otherMessage)
    }
}