package com.example.chatui.fragments

import SessionManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.example.chatui.R
import com.example.chatui.databinding.FragmentMoreBinding
import com.example.chatui.models.FullTeam
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TEAM_ID = "param1"


class MoreFragment : Fragment() {

    private var teamId: String? = null
    private lateinit var binding: FragmentMoreBinding
    private lateinit var session: SessionManager

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
        requireActivity().findViewById<FrameLayout>(R.id.chatFrameLayout).visibility = View.GONE
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.remove(this@MoreFragment)
        fragmentTransaction.commit()
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
                    showTeamDetails(team!!)
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
            .placeholder(R.drawable.color7)
            .centerCrop()
            .into(teamImage)


        val members = binding.usersList
        team.members?.forEach() { member ->
            val memberView = layoutInflater.inflate(R.layout.card_chat_item_group, members, false)
            val memberName = memberView.findViewById<TextView>(R.id.memberName)
            val memberUsername = memberView.findViewById<TextView>(R.id.memberNick)
            val memberImage = memberView.findViewById<ImageView>(R.id.memberImage)

            memberName.text = member.username
            memberUsername.text = member.nickname

            Glide
                .with(requireContext())
                .load(member.imgUrl)
                .placeholder(R.drawable.color7)
                .centerCrop()
                .into(memberImage)

            members.addView(memberView)
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)

        fetchTeamDetails()

        val backButton = binding.moreBackButton
        backButton.setOnClickListener {
            backButton.animation = android.view.animation.AnimationUtils.loadAnimation(requireContext(), androidx.appcompat.R.anim.abc_slide_in_top)
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