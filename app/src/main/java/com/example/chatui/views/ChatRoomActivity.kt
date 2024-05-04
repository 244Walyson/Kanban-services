package com.example.chatui.views

import SessionManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.bumptech.glide.Glide
import com.example.chatui.MainActivity
import com.example.chatui.R
import com.example.chatui.configs.WebSocketConfig
import com.example.chatui.databinding.ActivityChatRoomBinding
import com.example.chatui.fragments.MoreFragment
import com.example.chatui.fragments.ProfileFragment
import com.example.chatui.models.FcmToken
import com.example.chatui.models.Team
import com.example.chatui.notification.MessageNotification
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        motionLayout.findViewById<Button>(R.id.button_search).setOnClickListener {
            openSearchFromMenu()
        }


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

        setSearch()
        setFilter()

        binding.userName.setOnClickListener {
            startProfileFragment();
        }

        if(intent.getStringExtra("search").equals("true"))
            openSearchFromMenu()

    }

    private fun openSearchFromMenu() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        imm.showSoftInput(edtText, InputMethodManager.SHOW_IMPLICIT)

        search.setOnClickListener {
            if(name.text.isBlank() && edtText.text.isBlank()) {
                name.text = "Chat"
                searchBox.background = null
                searchBar.removeAllViews()
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
        }

    }
    private fun setFilter() {
        val all = binding.txtAll
        val teams = binding.txtTeams
        val direct = binding.txtDirect

        all.setOnClickListener {
            all.setTextColor(resources.getColor(R.color.white))
            teams.setTextColor(resources.getColor(R.color.gray_tertiary))
            direct.setTextColor(resources.getColor(R.color.gray_tertiary))
        }
        teams.setOnClickListener {
            all.setTextColor(resources.getColor(R.color.gray_tertiary))
            teams.setTextColor(resources.getColor(R.color.white))
            direct.setTextColor(resources.getColor(R.color.gray_tertiary))
        }
        direct.setOnClickListener {
            all.setTextColor(resources.getColor(R.color.gray_tertiary))
            teams.setTextColor(resources.getColor(R.color.gray_tertiary))
            direct.setTextColor(resources.getColor(R.color.white))
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