package com.example.chatui.fragments

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.example.chatui.R
import com.example.chatui.databinding.FragmentMoreBinding
import com.example.chatui.models.FullTeam
import com.example.chatui.views.ChatActivity
import com.example.chatui.views.ChatRoomActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TEAM_ID = "param1"


class MoreFragment : Fragment() {

    private var teamId: String? = null
    private lateinit var binding: FragmentMoreBinding
    private lateinit var session: SessionManager
    private lateinit var fullTeam: FullTeam

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMoreBinding.inflate(layoutInflater)
        session = SessionManager(requireContext())
        arguments?.let {
            teamId = it.getString(TEAM_ID)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            returnToActivity()
        }
    }

    private fun returnToActivity() {
        if (isAdded) {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("teamId", teamId)
            startActivity(intent)
            return
        }
    }

    private fun fetchTeamDetails() {
        val service = NetworkUtils.createServiceTeam()
        val token = session.accessToken

        Log.i("MORE FRAG", "TOKEN $token")

        service.getTeam(teamId!!, token!!)
            .enqueue(object : Callback<FullTeam> {
                override fun onResponse(call: Call<FullTeam>, response: Response<FullTeam>) {
                    if (response.isSuccessful) {
                        val team = response.body()
                        Log.i("MORE FRAG", "Team details: ${team?.name}")
                        fullTeam = team!!
                        showTeamDetails(team)
                    }
                    Log.i("MORE FRAG", "Team details: $response")
                }

                override fun onFailure(call: Call<FullTeam>, t: Throwable) {
                    Log.e("MORE FRAG", "Error fetching team details", t)
                }
            })
    }

    private fun showTeamDetails(team: FullTeam) {

        Log.i("MORE FRAG", "Team details: ${team.name}")
        Log.i("MORE FRAG", "Team details: ${team.imgUrl}")
        Log.i("MORE FRAG", "Team details: ${team.description}")
        Log.i("MORE FRAG", "Team details: ${team.occupationArea}")

        val name = binding.teamName
        name.text = team.name


        val description = binding.teamDescription
        description.text = team.description

        val teamImage = binding.teamImage
        Glide
            .with(requireContext())
            .load(team.imgUrl)
            .placeholder(R.drawable.color5)
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

            Log.i("MORE FRAG", "Member details: ${member.imgUrl}")

            Glide
                .with(requireContext())
                .load(member.imgUrl)
                .placeholder(R.drawable.color5)
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
                linearLayout = LinearLayout(requireContext())
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
                Log.i("AQUI", "AQUI $i size ${boards.size}")
                listMembers.addView(linearLayout)
                linearLayout = null
                i++
                return@forEachIndexed
            }
            i++
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)

        fetchTeamDetails()
        val teamMembers = binding.teamMembers
        val teamBoards = binding.teamBoards

        teamMembers.setOnClickListener {
            teamBoards.setTextColor(resources.getColor(R.color.gray_tertiary))
            teamMembers.setTextColor(resources.getColor(R.color.white))
            listMembers()
        }
        teamBoards.setOnClickListener {
            teamMembers.setTextColor(resources.getColor(R.color.gray_tertiary))
            teamBoards.setTextColor(resources.getColor(R.color.white))
            listBoards()
        }

        val backButton = binding.moreBackButton
        backButton.setOnClickListener {
            backButton.animation = android.view.animation.AnimationUtils.loadAnimation(
                requireContext(),
                androidx.appcompat.R.anim.abc_slide_in_top
            )
            returnToActivity()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(teamId: String) =
            MoreFragment().apply {
                arguments = Bundle().apply {
                    putString(TEAM_ID, teamId)
                }
            }
    }
}