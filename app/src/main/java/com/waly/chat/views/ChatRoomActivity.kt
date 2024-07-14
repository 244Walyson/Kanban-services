package com.waly.chat.views

import SessionManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.res.colorResource
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.waly.chat.MainActivity
import com.waly.chat.R
import com.waly.chat.configs.WebSocketConfig
import com.waly.chat.databinding.ActivityChatRoomBinding
import com.waly.chat.models.Team
import com.waly.chat.notification.MessageNotification
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.waly.chat.models.TeamFullResponse
import com.waly.chat.models.TeamMin
import com.waly.chat.models.User
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
import java.util.Date
import java.util.concurrent.TimeUnit

val PEOPLE_ACTIVE = "peopleActive"
val TEAMS_ACTIVE = "teamActive"
val CONNECTED_ACTIVE = "connectedActive"

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var session: SessionManager
    private lateinit var motionLayout: MotionLayout
    private lateinit var allChat: MutableList<Team>
    private lateinit var searchActive: String
    private lateinit var userSearch: MutableList<User>
    private lateinit var teamSearch: MutableList<TeamMin>
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatRoomBinding.inflate(layoutInflater)

        setContentView(binding.root)
        userSearch = mutableListOf()
        teamSearch = mutableListOf()
        allChat = mutableListOf()
        searchActive = PEOPLE_ACTIVE

        session = SessionManager(applicationContext)


        motionLayout = findViewById(R.id.motion_layout_id)

        motionLayout.findViewById<Button>(R.id.button_home).setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
            finish()
        }

        motionLayout.findViewById<Button>(R.id.button_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
        motionLayout.findViewById<Button>(R.id.button_search).setOnClickListener {
            openSearchFromMenu()
            finish()
        }

        onBackPressedDispatcher.addCallback {
            startActivity(Intent(this@ChatRoomActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this@ChatRoomActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }

        setSearch()
        setFilter()

        binding.userName.setOnClickListener {
            //startActivity(Intent(this, ProfileActivity::class.java))
        }

        if (intent.getStringExtra("search").equals("true")) {
            websocketConnect(true)
            openSearchFromMenu()
        } else websocketConnect(false)

    }

    private fun websocketConnect(fromSearch: Boolean) {
        allChat.clear()
        val nickname = session.userLogged
        val webSocketConfig = WebSocketConfig(applicationContext)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val session: StompSession = withContext(Dispatchers.IO) {
                    webSocketConfig.connect()
                }
                val subs: Flow<String> = session.subscribeText("/user/${nickname}/queue/chats")

                subs.collectLatest {
                    allChat.addAll(0, jsonStringToTeamList(it))
                    if (!fromSearch || count > 0) {
                        removeDuplicates(allChat.first().id)
                        val handler = Handler(Looper.getMainLooper())
                        val runnable = object : Runnable {
                            override fun run() {
                                showChatRooms(allChat)
                                handler.postDelayed(this, 30000)
                            }
                        }
                        handler.post(runnable)
                    }
                }
            } catch (e: Exception) {
                Log.e("STOMP", "Error: " + e.message + e.stackTraceToString())
            }
        }
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
        val scrollContainer = binding.motionLayoutContainer
        val scrollSearch = layoutInflater.inflate(R.layout.search_list, scrollContainer, false)
        inflateSearchList(scrollSearch, scrollContainer)
        inflateSearchList(scrollSearch, scrollContainer)
        edtText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Não precisamos fazer nada aqui
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Não precisamos fazer nada aqui
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Atualizar a query sempre que o texto for alterado
                val query = s.toString()
                when (searchActive) {
                    PEOPLE_ACTIVE -> listPeoples(query, scrollSearch, scrollContainer)
                    TEAMS_ACTIVE -> listTeams(query, scrollSearch, scrollContainer)
                    CONNECTED_ACTIVE -> listConnected(query, scrollSearch, scrollContainer)
                }
            }
        })
        search.setOnClickListener {
            if (name.text.isBlank() && edtText.text.isBlank()) {
                name.text = "Chat"
                searchBox.background = null
                searchBar.removeAllViews()
                startActivity(Intent(this, ChatRoomActivity::class.java))
                return@setOnClickListener
            }
            if (!edtText.text.isBlank()) {

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
            if (name.text.isBlank() && edtText.text.isBlank()) {
                name.text = "Chat"
                searchBox.background = null
                searchBar.removeAllViews()
                startActivity(Intent(this, ChatRoomActivity::class.java))
                return@setOnClickListener
            }
            if (!edtText.text.isBlank()) {
                return@setOnClickListener
            }
            name.text = ""
            searchBox.setBackgroundResource(R.drawable.search_bar_shape)
            searchBar.addView(edtView)
            edtText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edtText, InputMethodManager.SHOW_IMPLICIT)
            val scrollContainer = binding.motionLayoutContainer
            val scrollSearch = layoutInflater.inflate(R.layout.search_list, scrollContainer, false)
            inflateSearchList(scrollSearch, scrollContainer)
            edtText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Não precisamos fazer nada aqui
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Não precisamos fazer nada aqui
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Atualizar a query sempre que o texto for alterado
                    val query = s.toString()

                    when (searchActive) {
                        PEOPLE_ACTIVE -> listPeoples(query, scrollSearch, scrollContainer)
                        TEAMS_ACTIVE -> listTeams(query, scrollSearch, scrollContainer)
                        CONNECTED_ACTIVE -> listConnected(query, scrollSearch, scrollContainer)
                    }
                }
            })
        }

    }

    private fun inflateSearchList(scrollSearch: View, scrollContainer: LinearLayout) {
        listPeoples(null, scrollSearch, scrollContainer)
        searchActive = PEOPLE_ACTIVE
        addFunctionsToSearchListButtons(scrollSearch, scrollContainer)
    }

    private fun addFunctionsToSearchListButtons(scrollSearch: View, scrollContainer: LinearLayout) {
        val peoples = scrollSearch.findViewById<TextView>(R.id.txtPeople)
        val teams = scrollSearch.findViewById<TextView>(R.id.txtTeams)
        val connected = scrollSearch.findViewById<TextView>(R.id.txtConnected)
        val swipeRefresh = scrollSearch.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        peoples.setOnClickListener {
            searchActive = PEOPLE_ACTIVE
            peoples.setTextColor(resources.getColor(R.color.white))
            teams.setTextColor(resources.getColor(R.color.gray_tertiary))
            connected.setTextColor(resources.getColor(R.color.gray_tertiary))
            listPeoples(null, scrollSearch, scrollContainer)
            swipeRefresh.setOnRefreshListener {
                searchActive = PEOPLE_ACTIVE
                listPeoples(null, scrollSearch, scrollContainer)
                swipeRefresh.isRefreshing = false
            }
        }
        teams.setOnClickListener {
            searchActive = TEAMS_ACTIVE
            peoples.setTextColor(resources.getColor(R.color.gray_tertiary))
            teams.setTextColor(resources.getColor(R.color.white))
            connected.setTextColor(resources.getColor(R.color.gray_tertiary))
            listTeams(null, scrollSearch, scrollContainer)
            swipeRefresh.setOnRefreshListener {
                searchActive = TEAMS_ACTIVE
                listTeams(null, scrollSearch, scrollContainer)
                swipeRefresh.isRefreshing = false
            }
        }
        connected.setOnClickListener {
            searchActive = CONNECTED_ACTIVE
            peoples.setTextColor(resources.getColor(R.color.gray_tertiary))
            teams.setTextColor(resources.getColor(R.color.gray_tertiary))
            connected.setTextColor(resources.getColor(R.color.white))
            listConnected(null, scrollSearch, scrollContainer)
            swipeRefresh.setOnRefreshListener {
                searchActive = CONNECTED_ACTIVE
                if (allChat.isEmpty()) websocketConnect(true)
                listConnected(null, scrollSearch, scrollContainer)
                swipeRefresh.isRefreshing = false
            }
        }
        onBackPressedDispatcher.addCallback {
            startActivity(Intent(this@ChatRoomActivity, ChatRoomActivity::class.java))
        }
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, ChatRoomActivity::class.java))
            finish()
        }
    }

    private fun listPeoples(query: String?, scrollSearch: View, scrollContainer: LinearLayout) {
        val service = NetworkUtils.createServiceUser()
        if (userSearch.isEmpty()) {
            val token = session.accessToken
            service.getUsers(token!!)
                .enqueue(object : Callback<List<User>> {
                    override fun onResponse(
                        call: Call<List<User>>,
                        response: Response<List<User>>
                    ) {
                        if (response.isSuccessful) {
                            val users = response.body()
                            userSearch = users as MutableList<User>
                            showUsers(userSearch, scrollSearch, scrollContainer)
                        }
                    }
                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        Log.e(TAG, "Error: ${t.message}")
                    }
                });
        }
        if (query != null) {
            showUsers(userSearch.filter {
                it.nickname.uppercase().contains(query.uppercase()) || it.username.uppercase()
                    .contains(query.uppercase())
            }, scrollSearch, scrollContainer)
            return
        }
        showUsers(userSearch, scrollSearch, scrollContainer)
    }

    private fun showUsers(users: List<User>, scrollSearch: View, scrollContainer: LinearLayout) {
        val token = session.accessToken
        val myNickname = session.userLogged
        val searchList = scrollSearch.findViewById<LinearLayout>(R.id.searchList)
        searchList.removeAllViews()
        users.forEach { user ->
            if (user.nickname.equals(myNickname)) return@forEach
            val userCard = layoutInflater.inflate(R.layout.card_chat_item_search, scrollContainer, false)
            val userName = userCard.findViewById<TextView>(R.id.userName)
            val userImage = userCard.findViewById<LinearLayout>(R.id.cardImage)
            val userNickname = userCard.findViewById<TextView>(R.id.userNickname)
            val imgLayout = layoutInflater.inflate(R.layout.home_image, userImage, false)
            val btnConnect = userCard.findViewById<ImageView>(R.id.connectButton)
            userName.text = user.username
            userNickname.text = user.nickname
            Glide
                .with(applicationContext)
                .load(user.imgUrl)
                .placeholder(R.drawable.unknow_image)
                .centerCrop()
                .into(imgLayout.findViewById(R.id.statusImage))

            btnConnect.setOnClickListener {
                btnConnect.setImageResource(R.drawable.icon_more_black)

                requestConnection(user.id, token!!)
            }
            if (user.isConnected) {
                btnConnect.setImageResource(R.drawable.icon_chat)
                btnConnect.setOnClickListener {
                    val intent =
                        Intent(this@ChatRoomActivity, ChatActivity::class.java)
                    intent.putExtra("teamId", user.connectionId)
                    startActivity(intent)
                }
            }
            userImage.addView(imgLayout)
            searchList.addView(userCard)
        }
        scrollContainer.removeAllViews()
        scrollContainer.addView(scrollSearch)
    }

    private fun listTeams(query: String?, scrollSearch: View, scrollContainer: LinearLayout) {
        val token = session.accessToken
        val service = NetworkUtils.createServiceTeam()
        if (teamSearch.isEmpty()) {
            service.getTeams(token!!)
                .enqueue(object : Callback<TeamFullResponse> {
                    override fun onResponse(
                        call: Call<TeamFullResponse>,
                        response: Response<TeamFullResponse>
                    ) {
                        if (response.isSuccessful) {
                            val teams = response.body()?.content
                            teamSearch = teams as MutableList<TeamMin>
                            showTeams(teamSearch, scrollSearch, scrollContainer)
                        }
                    }
                    override fun onFailure(call: Call<TeamFullResponse>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        }
        if (query != null) {
            showTeams(teamSearch.filter {
                it.roomName.uppercase().contains(query.uppercase()) || it.description.uppercase()
                    .contains(query.uppercase())
            }, scrollSearch, scrollContainer)
            return
        }
        showTeams(teamSearch, scrollSearch, scrollContainer)
    }

    private fun showTeams(teams: List<TeamMin>, scrollSearch: View, scrollContainer: LinearLayout) {
        val searchList = scrollSearch.findViewById<LinearLayout>(R.id.searchList)
        searchList.removeAllViews()
        teams.forEach { team ->
            val userCard =
                layoutInflater.inflate(R.layout.card_chat_item_search, scrollContainer, false)
            val userName = userCard.findViewById<TextView>(R.id.userName)
            val userImage = userCard.findViewById<LinearLayout>(R.id.cardImage)
            val userNickname = userCard.findViewById<TextView>(R.id.userNickname)
            val imgLayout =
                layoutInflater.inflate(R.layout.home_image, userImage, false)
            val btnConnect = userCard.findViewById<ImageView>(R.id.connectButton)
            userName.text = team.roomName
            userNickname.text = team.description
            Glide
                .with(applicationContext)
                .load(team.imgUrl)
                .placeholder(R.drawable.unknow_image)
                .centerCrop()
                .into(imgLayout.findViewById(R.id.statusImage))

            btnConnect.setImageResource(R.drawable.icon_more_black)
            btnConnect.setOnClickListener {
                val intent = Intent(this@ChatRoomActivity, ChatActivity::class.java)
                intent.putExtra("teamId", team.id)
                startActivity(intent)
            }
            userImage.addView(imgLayout)
            searchList.addView(userCard)
        }
        scrollContainer.removeAllViews()
        scrollContainer.addView(scrollSearch)
    }

    private fun listConnected(query: String?, scrollSearch: View, scrollContainer: LinearLayout) {
        if (query != null) {
            showConnected(
                allChat.filter { it.roomName.uppercase().contains(query.uppercase()) },
                scrollSearch,
                scrollContainer
            )
            return;
        }
        showConnected(allChat, scrollSearch, scrollContainer)
    }

    private fun showConnected(allChatFilter: List<Team>, scrollSearch: View, scrollContainer: LinearLayout) {
        val searchList = scrollSearch.findViewById<LinearLayout>(R.id.searchList)
        searchList.removeAllViews()
        allChatFilter.forEach { user ->
            val userCard =
                layoutInflater.inflate(R.layout.card_chat_item_search, scrollContainer, false)
            val userName = userCard.findViewById<TextView>(R.id.userName)
            val userImage = userCard.findViewById<LinearLayout>(R.id.cardImage)
            val userNickname = userCard.findViewById<TextView>(R.id.userNickname)
            val imgLayout = layoutInflater.inflate(R.layout.home_image, userImage, false)
            val btnConnect = userCard.findViewById<ImageView>(R.id.connectButton)

            userName.text = user.roomName
            userNickname.text = user.latestMessage
            Glide
                .with(applicationContext)
                .load(user.imgUrl)
                .placeholder(R.drawable.unknow_image)
                .centerCrop()
                .into(imgLayout.findViewById(R.id.statusImage))

            btnConnect.setImageResource(R.drawable.icon_chat)
            btnConnect.setOnClickListener {
                val intent = Intent(this@ChatRoomActivity, ChatActivity::class.java)
                intent.putExtra("teamId", user.id)
                startActivity(intent)
            }
            userImage.addView(imgLayout)
            searchList.addView(userCard)
        }
        scrollContainer.removeAllViews()
        scrollContainer.addView(scrollSearch)
    }

    private fun requestConnection(userId: String, token: String) {
        val service = NetworkUtils.createServiceUser()
        service.requestConnection(token, userId)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Connection request sent",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e(TAG, "Error: ${t.message}")
                }
            })
    }

    private fun setFilter() {
        val all = motionLayout.findViewById<TextView>(R.id.txtAll)
        val teams = motionLayout.findViewById<TextView>(R.id.txtTeams)
        val direct = motionLayout.findViewById<TextView>(R.id.txtDirect)
        all.setOnClickListener {
            all.setTextColor(resources.getColor(R.color.white))
            teams.setTextColor(resources.getColor(R.color.gray_tertiary))
            direct.setTextColor(resources.getColor(R.color.gray_tertiary))
            listAll()
        }
        teams.setOnClickListener {
            all.setTextColor(resources.getColor(R.color.gray_tertiary))
            teams.setTextColor(resources.getColor(R.color.white))
            direct.setTextColor(resources.getColor(R.color.gray_tertiary))
            filterTeams()
        }
        direct.setOnClickListener {
            all.setTextColor(resources.getColor(R.color.gray_tertiary))
            teams.setTextColor(resources.getColor(R.color.gray_tertiary))
            direct.setTextColor(resources.getColor(R.color.white))
            filterDirect()
        }
    }

    private fun listAll() {
        showChatRooms(allChat)
    }

    private fun filterTeams() {
        val filterList = allChat.filter { it.id.contains("R") }
        showChatRooms(filterList.toMutableList())
    }

    private fun filterDirect() {
        val filterList = allChat.filter { it.id.contains("U") }
        showChatRooms(filterList.toMutableList())
    }


    fun jsonStringToTeamList(jsonString: String): MutableList<Team> {
        val gson = Gson()
        val listType = object : TypeToken<List<Team>>() {}.type
        var teams: MutableList<Team> = gson.fromJson(jsonString, listType)
        return teams
    }

    fun removeDuplicates(chatId: String) {
        val chatToRemove = allChat.filter { it.id == chatId }

        if (chatToRemove.size > 1) {
            val lastChat = chatToRemove.last()
            allChat.remove(lastChat)
        }
    }


    fun showChatRooms(teams: MutableList<Team>) {
        teams.forEach {
            Log.i("TEAM", it.roomName)
        }
        val scrollContainer = binding.motionLayoutContainer
        val scrollView = motionLayout.findViewById<LinearLayout>(R.id.ScrollChats)
        scrollView.removeAllViews()
        teams.forEach { team ->
            val roomCard = layoutInflater.inflate(R.layout.card_chat_item, scrollView, false)
            val cardImage = layoutInflater.inflate(R.layout.home_image, scrollContainer, false)

            val teamName = roomCard.findViewById<TextView>(R.id.text_group_name)
            teamName.text = team.roomName

            val latestMsg = roomCard.findViewById<TextView>(R.id.text_latest_message)
            latestMsg.text = team.latestMessage

            val latestActivity = roomCard.findViewById<TextView>(R.id.text_last_activity)
            var latestText = getTimeAgo(parseDateToMillis(team.lastActivity))
            if (latestText.contains("agora")) latestActivity.setTextColor(getColor(R.color.blue))
            if (latestText.contains("none")) latestActivity.visibility = View.GONE
            else latestActivity.setTextColor(resources.getColor(R.color.gray_tertiary))
            latestActivity.text = latestText

            val unreadMessages = roomCard.findViewById<TextView>(R.id.text_unread_messages)
            unreadMessages.visibility = View.GONE

            val image = cardImage.findViewById<ShapeableImageView>(R.id.statusImage)
            image.setOnClickListener {
                //startMoreFragment(team.id)
            }

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

            scrollView.addView(roomCard)
        }
        scrollContainer.removeAllViews()
        scrollContainer.addView(motionLayout)
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
                if (diffWeeks == 1L) "$diffWeeks semana atrás"
                else if (diffWeeks > 1000) "none"
                 else "$diffWeeks semanas atrás"
            }
        }
    }
}