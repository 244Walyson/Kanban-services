package com.waly.chat.views

import SessionManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.bumptech.glide.Glide
import com.waly.chat.MainActivity
import com.waly.chat.R
import com.waly.chat.configs.WebSocketConfig
import com.waly.chat.databinding.ActivityChatRoomBinding
import com.waly.chat.fragments.MoreFragment
import com.waly.chat.fragments.ProfileFragment
import com.waly.chat.models.FcmToken
import com.waly.chat.models.Team
import com.waly.chat.notification.MessageNotification
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.messaging.FirebaseMessaging
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

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var session: SessionManager
    private lateinit var motionLayout: MotionLayout
    private lateinit var allChat: MutableList<Team>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatRoomBinding.inflate(layoutInflater)

        setContentView(binding.root)

        session = SessionManager(applicationContext)


        motionLayout = findViewById(R.id.motion_layout_id)

        motionLayout.findViewById<Button>(R.id.button_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        motionLayout.findViewById<Button>(R.id.button_profile).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        motionLayout.findViewById<Button>(R.id.button_search).setOnClickListener {
            setSearch()
        }

        onBackPressedDispatcher.addCallback {
            startActivity(Intent(this@ChatRoomActivity, MainActivity::class.java))
        }


        MessageNotification(this).createNotificationChannel()

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        setSearch()
        //setFilter()

        binding.userName.setOnClickListener {
            startProfileFragment();
        }

        if(intent.getStringExtra("search").equals("true"))
            openSearchFromMenu()
        else
            websocketConnect()

    }

    private fun openSearchFromMenu() {
        val search = binding.searchButton
        val name = binding.userName
        val searchBox = binding.searchBox
        val searchBar = binding.searchChat
        val edtView = layoutInflater.inflate(R.layout.search_bar, searchBar, false)
        val edtText = edtView.findViewById<EditText>(R.id.searchBarEdtText)


        name.text = ""
        searchBox.setBackgroundResource(R.drawable.search_bar_shape)
        searchBar.addView(edtView)
        edtText.requestFocus()

        inflateSeachList()
        search.setOnClickListener {
            if(name.text.isBlank() && edtText.text.isBlank()) {
                name.text = "Chat"
                searchBox.background = null
                searchBar.removeAllViews()
                startActivity(Intent(this, ChatRoomActivity::class.java))
                return@setOnClickListener
            }
            if(!edtText.text.isBlank()) {

                return@setOnClickListener
            }
        }
    }

    private fun setSearch() {
        val search = binding.searchButton
        val name = binding.userName
        val searchBox = binding.searchBox
        val searchBar = binding.searchChat
        val edtView = layoutInflater.inflate(R.layout.search_bar, searchBar, false)
        val edtText = edtView.findViewById<EditText>(R.id.searchBarEdtText)

        search.setOnClickListener {
            if(name.text.isBlank() && edtText.text.isBlank()) {
                name.text = "Chat"
                searchBox.background = null
                searchBar.removeAllViews()
                startActivity(Intent(this, ChatRoomActivity::class.java))
                return@setOnClickListener
            }
            if(!edtText.text.isBlank()) {
                return@setOnClickListener
            }
            name.text = ""
            searchBox.setBackgroundResource(R.drawable.search_bar_shape)
            searchBar.addView(edtView)
            edtText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edtText, InputMethodManager.SHOW_IMPLICIT)
            inflateSeachList()
        }

    }

    private fun inflateSeachList() {
        val scrollContainer = binding.motionLayoutContainer
        val scrollSearch = layoutInflater.inflate(R.layout.search_list, scrollContainer, false)
        onBackPressedDispatcher.addCallback {
            startActivity(Intent(this@ChatRoomActivity, ChatRoomActivity::class.java))
        }
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java))
        }

        scrollContainer.removeAllViews()
        scrollContainer.addView(scrollSearch)
    }

//    private fun setFilter() {
//        val all = binding.txtAll
//        val teams = binding.txtTeams
//        val direct = binding.txtDirect
//
//        all.setOnClickListener {
//            all.setTextColor(resources.getColor(R.color.white))
//            teams.setTextColor(resources.getColor(R.color.gray_tertiary))
//            direct.setTextColor(resources.getColor(R.color.gray_tertiary))
//            listAll()
//        }
//        teams.setOnClickListener {
//            all.setTextColor(resources.getColor(R.color.gray_tertiary))
//            teams.setTextColor(resources.getColor(R.color.white))
//            direct.setTextColor(resources.getColor(R.color.gray_tertiary))
//            filterTeams()
//        }
//        direct.setOnClickListener {
//            all.setTextColor(resources.getColor(R.color.gray_tertiary))
//            teams.setTextColor(resources.getColor(R.color.gray_tertiary))
//            direct.setTextColor(resources.getColor(R.color.white))
//            filterDirect()
//        }
//    }

    private fun listAll() {
        val scrollContainer = binding.motionLayoutContainer
        val scrollView = motionLayout.findViewById<LinearLayout>(R.id.ScrollChats)

        scrollView.removeAllViews()
        scrollContainer.removeAllViews()
        scrollContainer.addView(motionLayout)
    }

    private fun filterTeams() {
        val scrollContainer = binding.motionLayoutContainer
        val scrollView = motionLayout.findViewById<LinearLayout>(R.id.ScrollChats)

        scrollView.removeAllViews()
        scrollContainer.removeAllViews()
        scrollContainer.addView(motionLayout)
    }

    private fun filterDirect() {
        val scrollContainer = binding.motionLayoutContainer
        val scrollView = motionLayout.findViewById<LinearLayout>(R.id.ScrollChats)

        scrollView.removeAllViews()
        scrollContainer.removeAllViews()
        scrollContainer.addView(motionLayout)
    }



    private fun startProfileFragment() {
        binding.chatFrameLayout.visibility = View.VISIBLE

        val fragment = ProfileFragment.newInstance()

        val fragmentManager = supportFragmentManager

        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.chatFrameLayout, fragment)
        transaction.commit()
    }

    private fun startMoreFragment(teamId: String) {
        binding.chatFrameLayout.visibility = View.VISIBLE

        val fragment = MoreFragment.newInstance(teamId)

        val fragmentManager = supportFragmentManager

        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.chatFrameLayout, fragment)
        transaction.commit()
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
        val scrollContainer = binding.motionLayoutContainer
        val scrollView = motionLayout.findViewById<LinearLayout>(R.id.ScrollChats)

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
                image.setOnClickListener {
                    startMoreFragment(teams[i]!!.id)
                }

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

                scrollView.addView(roomCard)
            }
        }
        scrollContainer.removeAllViews()
        scrollContainer.addView(motionLayout)
    }


}