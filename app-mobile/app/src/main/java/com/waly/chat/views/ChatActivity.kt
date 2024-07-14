package com.waly.chat.views

import SessionManager
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.FOCUS_DOWN
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.waly.chat.R
import com.waly.chat.configs.WebSocketConfig
import com.waly.chat.databinding.ActivityChatBinding
import com.waly.chat.models.Message
import com.waly.chat.models.MessageSent
import com.waly.chat.models.TeamFull
import com.google.gson.Gson
import com.waly.chat.models.Sender
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
  private lateinit var scrollView: ScrollView
  private lateinit var teamId: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityChatBinding.inflate(layoutInflater)

    setContentView(binding.root)
    scrollView = binding.chatScrollView
    scrollView.isSmoothScrollingEnabled = true

    scrollView.post {
      scrollView.fullScroll(View.FOCUS_DOWN)
    }
    channelSubscribe()

    binding.btnSend.setOnClickListener {
      sendMessage()
    }

    onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    teamId = intent.getStringExtra("teamId").toString()

    scrollView.viewTreeObserver.addOnGlobalLayoutListener {
      val isKeyboardOpen = isKeyboardOpen(scrollView)
      if (isKeyboardOpen) {
        scrollView.post {
          scrollView.fullScroll(FOCUS_DOWN)
        }
      }
    }

  }

  private fun isKeyboardOpen(view: View): Boolean {
    val visibleBounds = Rect()
    view.getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = view.rootView.height - (visibleBounds.bottom - visibleBounds.top)
    val marginOfError = view.resources.displayMetrics.density * 100
    return heightDiff > marginOfError
  }

  private val onBackPressedCallback = object : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
      startActivity(Intent(this@ChatActivity, ChatRoomActivity::class.java))
      finish()
    }
  }


  fun sendMessage() {
    val message = binding.edtMessage.text.toString()
    if (message.isEmpty()) return;
    binding.edtMessage.text.clear()
    val websocketConfig = WebSocketConfig(applicationContext)
    val textMsg: MessageSent = MessageSent(message)
    val myMsg = Message("0", Sender("1", "1", "1", "1"), message, "1")
    showMyMessage(myMsg)
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
        showSimpMessage(arrayListOf(message))
      }
    }
  }

  fun showChatRoom(team: TeamFull) {
    val toolbar = binding.chatToolbar
    val newHeaderChat = layoutInflater.inflate(R.layout.header_chat, toolbar, false)
    toolbar.addView(newHeaderChat)
    val headerChat = layoutInflater.inflate(R.layout.header_chat, toolbar, false)
    val roomTitle = headerChat.findViewById<TextView>(R.id.room_name)
    roomTitle.background = null
    roomTitle.text = team.roomName
    val roomDesc = headerChat.findViewById<TextView>(R.id.room_description)
    roomDesc.text = team.description
    val peopleCount = headerChat.findViewById<TextView>(R.id.people_count)
    peopleCount.text = team.membersCount.toString()
    if (team.id.contains("R")) {
      peopleCount.visibility = View.VISIBLE
      headerChat.findViewById<ImageView>(R.id.people_icon).visibility = View.VISIBLE
    }
    var teamId =
      if (team.id.contains("R") || team.id.contains("U")) team.id.substring(1) else team.id
    val roomImage = headerChat.findViewById<ImageView>(R.id.userImage)
    Glide
      .with(applicationContext)
      .load(team.imgUrl)
      .placeholder(R.drawable.unknow_image)
      .centerCrop()
      .into(roomImage)
    roomImage.setOnClickListener {
      val intent = Intent(this, TeamActivity::class.java)
      intent.putExtra("teamId", teamId)
      startActivity(intent)
    }
    headerChat.findViewById<ImageView>(R.id.people_icon).setOnClickListener {
      val intent = Intent(this, TeamActivity::class.java)
      intent.putExtra("teamId", teamId)
      startActivity(intent)
    }
    roomTitle.setOnClickListener {
      val intent = Intent(this, TeamActivity::class.java)
      intent.putExtra("teamId", teamId)
      startActivity(intent)
    }
    headerChat.findViewById<ImageView>(R.id.chatBackButton).setOnClickListener {
      startActivity(Intent(this, ChatRoomActivity::class.java))
      finish()
    }
    binding.chatToolbar.removeAllViews()
    toolbar.addView(headerChat)
    showMessage(team.messages)
    scrollView.post {
      scrollView.fullScroll(View.FOCUS_DOWN)
    }
  }

  fun showMessage(message: List<Message>) {
    val myNickname = SessionManager(applicationContext).userLogged
    message.forEach { msg ->
      if (msg.sender.nickName.equals(myNickname)) {
        showMyMessage(msg)
        return@forEach
      }
      showOtherMessage(msg)
    }
    scrollView.post {
      scrollView.fullScroll(View.FOCUS_DOWN)
    }
  }

  fun showSimpMessage(message: List<Message>) {
    val myNickname = SessionManager(applicationContext).userLogged
    message.forEach { msg ->
      if (msg.sender.nickName.equals(myNickname)) {
        return@forEach
      }
      showOtherMessage(msg)
    }
    scrollView.post {
      scrollView.fullScroll(View.FOCUS_DOWN)
    }
  }

  fun showMyMessage(msg: Message) {
    val chatContainer = binding.chatContainer
    val myMessage = layoutInflater.inflate(R.layout.chat_item_my, chatContainer, false)
    val txtMsg = myMessage.findViewById<TextView>(R.id.chat_my)
    txtMsg.text = msg.content
    myMessage.tag = msg.id
    chatContainer.addView(myMessage)
    scrollView.post {
      scrollView.fullScroll(View.FOCUS_DOWN)
    }
  }


  fun showOtherMessage(msg: Message) {
    val chatContainer = binding.chatContainer
    val otherMessage = layoutInflater.inflate(R.layout.chat_item_other, chatContainer, false)
    val txtMsg = otherMessage.findViewById<TextView>(R.id.message_content)
    txtMsg.text = msg.content
    val senderName = otherMessage.findViewById<TextView>(R.id.sender_name)
    senderName.text = msg.sender.name
    senderName.setTextColor(getRandomColor())
    val senderImage = otherMessage.findViewById<ImageView>(R.id.senderImage)
    if (teamId.contains("U")) {
      senderName.visibility = View.GONE
      senderImage.visibility = View.GONE
    } else {
      Glide
        .with(applicationContext)
        .load(msg.sender.imgUrl)
        .centerCrop()
        .placeholder(R.drawable.unknow_image)
        .into(senderImage)
    }
    chatContainer.addView(otherMessage)
    scrollView.post {
      scrollView.fullScroll(View.FOCUS_DOWN)
    }
  }

  private fun getRandomColor(): Int {
    val r = (0..255).random()
    val g = (0..255).random()
    val b = (0..255).random()
    return 0xff000000.toInt() or (r shl 16) or (g shl 8) or b
  }
}