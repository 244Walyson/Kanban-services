package com.waly.chat.views

import SessionManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.waly.chat.R
import com.waly.chat.databinding.ActivityTeamBinding
import com.waly.chat.models.FullTeam
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamBinding
    private lateinit var session: SessionManager
    private lateinit var fullTeam: FullTeam

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        fullTeam = FullTeam()

        fetchTeamDetails(intent.getStringExtra("teamId"))

        val teamMembers = binding.teamMembers
        val teamBoards = binding.teamBoards
        teamMembers.setOnClickListener {
            teamMembers.setTextColor(getColor(R.color.white))
            teamBoards.setTextColor(getColor(R.color.gray_tertiary))
            listMembers()
        }
        teamBoards.setOnClickListener {
            teamMembers.setTextColor(getColor(R.color.gray_tertiary))
            teamBoards.setTextColor(getColor(R.color.white))
            listBoards()
        }


        binding.moreBackButton.setOnClickListener {
            finish()
        }

    }

    private fun fetchTeamDetails(teamId: String?) {
        val service = NetworkUtils.createServiceTeam()
        val token = session.accessToken

        service.getTeam(teamId!!, token!!)
            .enqueue(object : Callback<FullTeam> {
                override fun onResponse(call: Call<FullTeam>, response: Response<FullTeam>) {
                    if (response.isSuccessful) {
                        val team = response.body()
                        fullTeam = team!!
                        showTeamDetails(team)
                    }
                }

                override fun onFailure(call: Call<FullTeam>, t: Throwable) {
                    Log.e("MORE FRAG", "Error fetching team details", t)
                }
            })
    }

    private fun showTeamDetails(team: FullTeam) {
        val name = binding.teamName
        name.text = team.name
        val description = binding.teamDescription
        description.text = team.description
        val teamImage = binding.teamImage
        Glide
            .with(this)
            .load(team.imgUrl)
            .placeholder(R.drawable.unknow_image)
            .centerCrop()
            .into(teamImage)

        listMembers()

    }

    private fun listMembers() {
        val members = binding.usersList
        members.removeAllViews()

        fullTeam.members?.forEach() { member ->
            val memberView = layoutInflater.inflate(R.layout.card_chat_item_group, members, false)
            val memberName = memberView.findViewById<TextView>(R.id.memberName)
            val memberUsername = memberView.findViewById<TextView>(R.id.memberNick)
            val memberImage = memberView.findViewById<ImageView>(R.id.memberImage)

            memberName.text = member.username
            memberUsername.text = member.nickname

            Glide
                .with(this)
                .load(member.imgUrl)
                .placeholder(R.drawable.unknow_image)
                .centerCrop()
                .into(memberImage)

            members.addView(memberView)
        }
    }

    private fun listBoards() {
        val listMembers = binding.usersList
        listMembers.removeAllViews()

        var linearLayout: LinearLayout? = null
        var i = 1
        val boards = fullTeam.boards!!
        boards.forEachIndexed { index, board ->

            if (linearLayout == null) {
                linearLayout = LinearLayout(this)
                linearLayout!!.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                linearLayout!!.gravity = Gravity.CENTER
                if (i == boards.size) linearLayout!!.gravity = Gravity.START
            }

            val boardView = layoutInflater.inflate(R.layout.card_board, listMembers, false)
            val boardName = boardView.findViewById<TextView>(R.id.boardName)
            val totalCards = boardView.findViewById<TextView>(R.id.totalCards)

            boardName.text = board.title
            totalCards.text = board.totalCards.toString()
            linearLayout!!.addView(boardView)

            if (i % 2 == 0 || i == boards.size) {
                listMembers.addView(linearLayout)
                linearLayout = null
                i++
                return@forEachIndexed
            }
            i++
        }
    }
}