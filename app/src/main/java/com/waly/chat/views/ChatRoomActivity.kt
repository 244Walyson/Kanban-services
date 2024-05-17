package com.waly.chat.views

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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.waly.chat.MainActivity
import com.waly.chat.R
import com.waly.chat.configs.WebSocketConfig
import com.waly.chat.databinding.ActivityChatRoomBinding
import com.waly.chat.fragments.MoreFragment
import com.waly.chat.models.Team
import com.waly.chat.notification.MessageNotification
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.waly.chat.models.TeamFullResponse
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

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var session: SessionManager
    private lateinit var motionLayout: MotionLayout
    private lateinit var allChat: MutableList<Team>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatRoomBinding.inflate(layoutInflater)

        setContentView(binding.root)
        allChat = mutableListOf()

        session = SessionManager(applicationContext)


        motionLayout = findViewById(R.id.motion_layout_id)

        motionLayout.findViewById<Button>(R.id.button_home).setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        motionLayout.findViewById<Button>(R.id.button_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        motionLayout.findViewById<Button>(R.id.button_search).setOnClickListener {
            openSearchFromMenu()
        }

        onBackPressedDispatcher.addCallback {
            startActivity(Intent(this@ChatRoomActivity, MainActivity::class.java))
            finish()
        }


        MessageNotification(this).createNotificationChannel()

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setSearch()
        //setFilter()

        binding.userName.setOnClickListener {
            //startActivity(Intent(this, ProfileActivity::class.java))
        }

        if (intent.getStringExtra("search").equals("true")){
            websocketConnect(true)
            openSearchFromMenu()
        } else websocketConnect(false)

    }

    fun websocketConnect(fromSearch: Boolean) {
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
                    Log.i("STOMP", "Received message: $it")
                    allChat.addAll(0, jsonStringToTeamList(it))
                    if(!fromSearch) {
                        showChatRooms(allChat)
                        removeDuplicates(allChat.first().id)
                    }
                }


                Log.i("STOMP", "Connected to STOMP")


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

        inflateSeachList()
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
            inflateSeachList()
        }

    }

    private fun inflateSeachList() {
        val scrollContainer = binding.motionLayoutContainer
        val scrollSearch = layoutInflater.inflate(R.layout.search_list, scrollContainer, false)
        val searchList = scrollSearch.findViewById<LinearLayout>(R.id.searchList)
        val peoples = scrollSearch.findViewById<TextView>(R.id.txtPeople)
        val teams = scrollSearch.findViewById<TextView>(R.id.txtTeams)
        val connected = scrollSearch.findViewById<TextView>(R.id.txtConnected)
        val swipeRefresh = scrollSearch.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        listPeoples(searchList, scrollContainer, scrollSearch)
        swipeRefresh.setOnRefreshListener {
            scrollContainer.removeAllViews()
            searchList.removeAllViews()
            listPeoples(searchList, scrollContainer, scrollSearch)
            swipeRefresh.isRefreshing = false
        }


        peoples.setOnClickListener {
            scrollContainer.removeAllViews()
            searchList.removeAllViews()
            peoples.setTextColor(resources.getColor(R.color.white))
            teams.setTextColor(resources.getColor(R.color.gray_tertiary))
            connected.setTextColor(resources.getColor(R.color.gray_tertiary))
            listPeoples(searchList, scrollContainer, scrollSearch)
            swipeRefresh.setOnRefreshListener {
                searchList.removeAllViews()
                listPeoples(searchList, scrollContainer, scrollSearch)
                swipeRefresh.isRefreshing = false
            }
        }

        teams.setOnClickListener {
            scrollContainer.removeAllViews()
            searchList.removeAllViews()
            peoples.setTextColor(resources.getColor(R.color.gray_tertiary))
            teams.setTextColor(resources.getColor(R.color.white))
            connected.setTextColor(resources.getColor(R.color.gray_tertiary))
            listTeams(searchList, scrollContainer, scrollSearch)
            swipeRefresh.setOnRefreshListener {
                searchList.removeAllViews()
                listTeams(searchList, scrollContainer, scrollSearch)
                swipeRefresh.isRefreshing = false
            }
        }

        connected.setOnClickListener {
            scrollContainer.removeAllViews()
            searchList.removeAllViews()
            peoples.setTextColor(resources.getColor(R.color.gray_tertiary))
            teams.setTextColor(resources.getColor(R.color.gray_tertiary))
            connected.setTextColor(resources.getColor(R.color.white))
            listConnected(searchList, scrollContainer, scrollSearch)
            swipeRefresh.setOnRefreshListener {
                if(allChat.isEmpty()) websocketConnect(true)
                searchList.removeAllViews()
                scrollContainer.removeAllViews()
                listConnected(searchList, scrollContainer, scrollSearch)
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

    private fun listPeoples(
        searchList: LinearLayout,
        scrollContainer: LinearLayout,
        scrollSearch: View
    ) {
        val service = NetworkUtils.createServiceUser()
        val token = session.accessToken
        val myNickname = session.userLogged

        service.getUsers(token!!)
            .enqueue(object : Callback<List<User>> {
                override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                    if (response.isSuccessful) {
                        val users = response.body()
                        users?.forEach { user ->
                            if (user.nickname.equals(myNickname)) return@forEach
                            val userCard = layoutInflater.inflate(
                                R.layout.card_chat_item_search,
                                scrollContainer,
                                false
                            )
                            val userName = userCard.findViewById<TextView>(R.id.userName)
                            val userImage = userCard.findViewById<LinearLayout>(R.id.cardImage)
                            val userNickname = userCard.findViewById<TextView>(R.id.userNickname)
                            val imgLayout =
                                layoutInflater.inflate(R.layout.home_image, userImage, false)
                            val btnConnect = userCard.findViewById<ImageView>(R.id.connectButton)

                            userName.text = user.username
                            userNickname.text = user.nickname
                            Glide
                                .with(applicationContext)
                                .load(user.imgUrl)
                                .centerCrop()
                                .into(imgLayout.findViewById(R.id.statusImage))

                            btnConnect.setOnClickListener {
                                btnConnect.setImageResource(R.drawable.icon_more_black)

                                requestConnection(user.id, token)
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
                    }
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    Log.e(TAG, "Error: ${t.message}")
                }
            });
        scrollContainer.removeAllViews()
        scrollContainer.addView(scrollSearch)
    }

    private fun listTeams(
        searchList: LinearLayout,
        scrollContainer: LinearLayout,
        scrollSearch: View
    ) {
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
                        teams?.forEach { team ->
                            val userCard = layoutInflater.inflate(
                                R.layout.card_chat_item_search,
                                scrollContainer,
                                false
                            )
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
                    }
                }

                override fun onFailure(call: Call<TeamFullResponse>, t: Throwable) {
                    Log.e("Teams", "Failed to fetch teams")
                    Log.e("Teams", t.message!!)
                    Log.e("Teams", t.cause.toString())
                    t.printStackTrace()
                }
            })
        scrollContainer.removeAllViews()
        scrollContainer.addView(scrollSearch)
    }


    private fun listConnected(
        searchList: LinearLayout,
        scrollContainer: LinearLayout,
        scrollSearch: View
    ) {
        allChat.forEach { user ->
            Log.i("Connected", user.id)
            val userCard = layoutInflater.inflate(
                R.layout.card_chat_item_search,
                scrollContainer,
                false
            )
            val userName = userCard.findViewById<TextView>(R.id.userName)
            val userImage = userCard.findViewById<LinearLayout>(R.id.cardImage)
            val userNickname = userCard.findViewById<TextView>(R.id.userNickname)
            val imgLayout =
                layoutInflater.inflate(R.layout.home_image, userImage, false)
            val btnConnect = userCard.findViewById<ImageView>(R.id.connectButton)

            userName.text = user.roomName
            userNickname.text = user.latestMessage
            Glide
                .with(applicationContext)
                .load(user.imgUrl)
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
                        Log.i(TAG, "Connection request sent")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e(TAG, "Error: ${t.message}")
                }
            })

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


    private fun startMoreFragment(teamId: String) {
        binding.chatFrameLayout.visibility = View.VISIBLE

        val fragment = MoreFragment.newInstance(teamId)

        val fragmentManager = supportFragmentManager

        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.chatFrameLayout, fragment)
        transaction.commit()
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

            val image = cardImage.findViewById<ShapeableImageView>(R.id.statusImage)
            image.setOnClickListener {
                startMoreFragment(team.id)
            }

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

            scrollView.addView(roomCard)
        }
        scrollContainer.removeAllViews()
        scrollContainer.addView(motionLayout)
    }

}